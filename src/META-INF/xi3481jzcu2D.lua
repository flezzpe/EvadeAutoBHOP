local ContextActionService = cloneref(game:GetService('ContextActionService'))
local UserInputService = cloneref(game:GetService('UserInputService'))
local TweenService = cloneref(game:GetService('TweenService'))
local HttpService = cloneref(game:GetService('HttpService'))
local RunService = cloneref(game:GetService('RunService'))
local Lighting = cloneref(game:GetService('Lighting'))
local Players = cloneref(game:GetService('Players'))
local Debris = cloneref(game:GetService('Debris'))


local AcrylicBlur = {}
AcrylicBlur.__index = AcrylicBlur


local Connections = {}


function AcrylicBlur.new(object: GuiObject)
    local self = setmetatable({
        _object = object,
        _root = nil,
        _frame = nil,
        _binds = {},
    }, AcrylicBlur)

    self:create_blur()

    return self
end


function AcrylicBlur:create_blur()
    if not self._object then
        warn('failed to create blur on a nil instance')

        return
    end

    local old_root = workspace.CurrentCamera:FindFirstChild('AcrylicBlur')

    if old_root then
        Debris:AddItem(old_root, 0)
    end

    self._root = Instance.new('Folder', workspace.CurrentCamera)
    self._root.Name = 'AcrylicBlur'

    self:setup()
    self:render()
end


function AcrylicBlur:setup()
    local depth_of_fields = Lighting:FindFirstChild('AcrylicBlur') or Instance.new('DepthOfFieldEffect')
    depth_of_fields.FarIntensity = 0
    depth_of_fields.FocusDistance = 51.6
    depth_of_fields.InFocusRadius = 50
    depth_of_fields.NearIntensity = 1
    depth_of_fields.Name = 'AcrylicBlur'
    depth_of_fields.Parent = Lighting

    local frame = Instance.new('Frame')
    frame.Parent = self._object
    frame.Size = UDim2.new(0.95, 0, 0.95, 0)
    frame.Position = UDim2.new(0.5, 0, 0.5, 0)
    frame.AnchorPoint = Vector2.new(0.5, 0.5)
    frame.BackgroundTransparency = 1

    self._frame = frame
end


function AcrylicBlur:is_nan(value: any)
    return value ~= value
end


function AcrylicBlur:render()
    do
        local is_nan = AcrylicBlur:is_nan(workspace.CurrentCamera:ScreenPointToRay(0, 0).Origin.x)

        while is_nan do
            RunService.RenderStepped:wait()

            is_nan = AcrylicBlur:is_nan(workspace.CurrentCamera:ScreenPointToRay(0, 0).Origin.x)
        end
    end
    
    local draw_quad
    
    do
        local acos, max, pi, sqrt = math.acos, math.max, math.pi, math.sqrt
        local size = 0.2
    
        function draw_triangle(v1, v2, v3, p0, p1)
            local s1 = (v1 - v2).magnitude
            local s2 = (v2 - v3).magnitude
            local s3 = (v3 - v1).magnitude

            local smax = max(s1, s2, s3)
            local A, B, C

            if s1 == smax then
                A, B, C = v1, v2, v3
            elseif s2 == smax then
                A, B, C = v2, v3, v1
            elseif s3 == smax then
                A, B, C = v3, v1, v2
            end
    
            local para = ( (B-A).x*(C-A).x + (B-A).y*(C-A).y + (B-A).z*(C-A).z ) / (A-B).magnitude
            local perp = sqrt((C-A).magnitude^2 - para*para)
            local dif_para = (A - B).magnitude - para
    
            local st = CFrame.new(B, A)
            local za = CFrame.Angles(pi/2,0,0)
    
            local cf0 = st
    
            local Top_Look = (cf0 * za).lookVector
            local Mid_Point = A + CFrame.new(A, B).lookVector * para
            local Needed_Look = CFrame.new(Mid_Point, C).lookVector
            local dot = Top_Look.x*Needed_Look.x + Top_Look.y*Needed_Look.y + Top_Look.z*Needed_Look.z
    
            local ac = CFrame.Angles(0, 0, acos(dot))
    
            cf0 = cf0 * ac

            if ((cf0 * za).lookVector - Needed_Look).magnitude > 0.01 then
                cf0 = cf0 * CFrame.Angles(0, 0, -2*acos(dot))
            end

            cf0 = cf0 * CFrame.new(0, perp/2, -(dif_para + para/2))
    
            local cf1 = st * ac * CFrame.Angles(0, pi, 0)

            if ((cf1 * za).lookVector - Needed_Look).magnitude > 0.01 then
                cf1 = cf1 * CFrame.Angles(0, 0, 2*acos(dot))
            end

            cf1 = cf1 * CFrame.new(0, perp/2, dif_para/2)
    
            if not p0 then
                p0 = Instance.new('Part')
                p0.FormFactor = 'Custom'
                p0.TopSurface = 0
                p0.BottomSurface = 0
                p0.Anchored = true
                p0.CanCollide = false
                p0.CastShadow = false
                p0.Material = 'Glass'
                p0.Size = Vector3.new(size, size, size)

                local mesh = Instance.new('SpecialMesh', p0)
                mesh.MeshType = 2
                mesh.Name = 'WedgeMesh'
            end

			local WedgeMesh = p0:FindFirstChild('WedgeMesh')

			if not WedgeMesh or not p0 then
				return
			end

            p0.WedgeMesh.Scale = Vector3.new(0, perp/size, para/size)
            p0.CFrame = cf0
    
            if not p1 then
                p1 = p0:clone()
            end

            p1.WedgeMesh.Scale = Vector3.new(0, perp/size, dif_para/size)
            p1.CFrame = cf1
    
            return p0, p1
        end
    
        function draw_quad(v1, v2, v3, v4, parts)
            parts[1], parts[2] = draw_triangle(v1, v2, v3, parts[1], parts[2])
            parts[3], parts[4] = draw_triangle(v3, v2, v4, parts[3], parts[4])
        end
    end

    if self._binds[self._frame] then
        return self._binds[self._frame].parts
    end
    
    local parts = {}
    local f = Instance.new('Folder', self._root)
    f.Name = self._frame.Name
    
    local parents = {}

    local function add(child: any)
        if not child then
            return
        end

        if child:IsA('GuiObject') then
            parents[#parents + 1] = child
            add(child.Parent)
        end
    end

    add(self._frame)
    
    local function update_orientation(fetch_properties)
        local properties = {
            Transparency = 0.8,
            Color = Color3.new(30, 30, 30)
        }

        local zIndex = 1 - 0.05 * self._frame.ZIndex
    
        local tl, br = self._frame.AbsolutePosition, self._frame.AbsolutePosition + self._frame.AbsoluteSize
        local tr, bl = Vector2.new(br.x, tl.y), Vector2.new(tl.x, br.y)

        do
            local rot = 0;

            for _, v in parents do
                rot = rot + v.Rotation
            end

            if rot ~= 0 and rot % 180 ~= 0 then
                local mid = tl:lerp(br, 0.5)
                local s, c = math.sin(math.rad(rot)), math.cos(math.rad(rot))
                local vec = tl
                tl = Vector2.new(c*(tl.x - mid.x) - s*(tl.y - mid.y), s*(tl.x - mid.x) + c*(tl.y - mid.y)) + mid
                tr = Vector2.new(c*(tr.x - mid.x) - s*(tr.y - mid.y), s*(tr.x - mid.x) + c*(tr.y - mid.y)) + mid
                bl = Vector2.new(c*(bl.x - mid.x) - s*(bl.y - mid.y), s*(bl.x - mid.x) + c*(bl.y - mid.y)) + mid
                br = Vector2.new(c*(br.x - mid.x) - s*(br.y - mid.y), s*(br.x - mid.x) + c*(br.y - mid.y)) + mid
            end
        end

        draw_quad(
            workspace.CurrentCamera:ScreenPointToRay(tl.x, tl.y, zIndex).Origin, 
            workspace.CurrentCamera:ScreenPointToRay(tr.x, tr.y, zIndex).Origin, 
            workspace.CurrentCamera:ScreenPointToRay(bl.x, bl.y, zIndex).Origin, 
            workspace.CurrentCamera:ScreenPointToRay(br.x, br.y, zIndex).Origin, 
            parts
        )

        if fetch_properties then
            for _, pt in parts do
                pt.Parent = f
            end

            for propName, propValue in properties do
                for _, pt in parts do
                    pt[propName] = propValue
                end
            end
        end
    end

    update_orientation(true)
    Connections['AclyricBlur'] = RunService.RenderStepped:Connect(update_orientation)
end


local LocalPlayer = Players.LocalPlayer
local mouse = LocalPlayer:GetMouse()

function Connections:abadone()
	for _, connection in Connections do
		if typeof(connection) == 'function' then
			connection = nil

			continue
		end

		connection:Disconnect()
		connection = nil
	end
end

local Library = {
	can_be_optimized = false :: boolean,
	scale_cooldown = false :: boolean,
	open = true :: boolean,
	current_tab = nil,
	disconnected = false :: boolean,
	mobile = table.find({ Enum.Platform.IOS, Enum.Platform.Android }, UserInputService:GetPlatform()),
	flags = {} :: any,
	ui = nil :: any,
	scale = 0
}


local ConfigsController = {}

function ConfigsController.save(file_name: string, config: any)
	if not isfolder(`Nurysium`) then
		makefolder(`Nurysium`)
	end

	if not isfolder(`Nurysium/configs`) then
		makefolder(`Nurysium/configs`)
	end

	local flags = HttpService:JSONEncode(config)

	writefile(`Nurysium/configs/{file_name}.json`, flags)
end

function ConfigsController.load(file_name: any, config: any)
	if not isfile('Nurysium/configs/' .. file_name .. '.json') then
		ConfigsController.save(file_name, config)

		return
	end

	local flags = readfile('Nurysium/configs/' .. file_name .. '.json')

	if not flags then
		ConfigsController.save(file_name, config)

		return
	end

	return HttpService:JSONDecode(flags)
end

local current_config = ConfigsController.load(game.GameId, Library.flags)
Library.flags = current_config

if not Library.flags then
	Library.flags = {}
end

local UIManager = {}

function UIManager.refresh_tabs(Tab: TextButton)
	TweenService:Create(Tab, TweenInfo.new(0.7, Enum.EasingStyle.Exponential, Enum.EasingDirection.Out), {
		BackgroundTransparency = 0.9
	}):Play()

	local Title = Tab:FindFirstChild('Title')
	local Icon = Tab:FindFirstChild('Icon')

	TweenService:Create(Title, TweenInfo.new(1, Enum.EasingStyle.Exponential), {
		TextTransparency = 0
	}):Play()

	TweenService:Create(Icon, TweenInfo.new(1, Enum.EasingStyle.Exponential), {
		ImageTransparency = 0
	}):Play()

	for _, object in Library.ui.Background.Tabs:GetChildren() do
		if object:IsA('TextButton') and object ~= Tab then
			TweenService:Create(object, TweenInfo.new(0.25, Enum.EasingStyle.Exponential, Enum.EasingDirection.Out), {
				BackgroundTransparency = 1
			}):Play()

			TweenService:Create(object.Title, TweenInfo.new(0.85, Enum.EasingStyle.Exponential), {
				TextTransparency = 0.8
			}):Play()

			TweenService:Create(object.Icon, TweenInfo.new(0.85, Enum.EasingStyle.Exponential), {
				ImageTransparency = 0.8
			}):Play()
		end
	end
end

function UIManager.refresh_sections(right_section: ScrollingFrame, left_section: ScrollingFrame)
	for _, object in Library.ui.Background.Sections:GetChildren() do
		if object == left_section or object == right_section then
			object.Visible = true

			continue
		end

		object.Visible = false
	end
end

function UIManager.animate_sections(right_section: ScrollingFrame, left_section: ScrollingFrame)
	local right_list_layout = right_section:FindFirstChildOfClass('UIListLayout')
	local left_list_layout = left_section:FindFirstChildOfClass('UIListLayout')

	right_list_layout.Padding = UDim.new(0, 500)
	left_list_layout.Padding = UDim.new(0, 500)

	TweenService:Create(right_list_layout, TweenInfo.new(1, Enum.EasingStyle.Exponential), {
		Padding = UDim.new(0, 6)
	}):Play()

	TweenService:Create(left_list_layout, TweenInfo.new(1, Enum.EasingStyle.Exponential), {
		Padding = UDim.new(0, 6)
	}):Play()
end

function Library:get_screen_scale()
	if Library.disconnected then
		return
	end

	local viewport_size_x = workspace.CurrentCamera.ViewportSize.X
	local viewport_size_y = workspace.CurrentCamera.ViewportSize.Y

	local screen_size = (viewport_size_x + viewport_size_y) / 3000

	if Library.mobile then
		screen_size = (viewport_size_x + viewport_size_y) / 7000
	end

	Library.scale = screen_size + math.max(0.95 - screen_size, 0)
end

function Library:changed()
	Library.ui.AncestryChanged:Once(self)
end

function Library.normalize_size()	
	Library.scale_cooldown = true

	if not Library.open then
		Library.scale = 0

		TweenService:Create(Library.ui.Background.UIScale, TweenInfo.new(0.7, Enum.EasingStyle.Back, Enum.EasingDirection.InOut), {
			Scale = Library.scale
		}):Play()

		task.delay(0.6, function()
			Library.ui.Enabled = false
		end)

		task.delay(1, function()
			Library.scale_cooldown = false
		end)

		return
	end

	Library.get_screen_scale()

	TweenService:Create(Library.ui.Background.UIScale, TweenInfo.new(0.95, Enum.EasingStyle.Back, Enum.EasingDirection.Out), {
		Scale = Library.scale
	}):Play()

	Library.ui.Enabled = true

	task.delay(1, function()
		Library.scale_cooldown = false
	end)
end

UserInputService.InputBegan:Connect(function(input: InputObject, event: boolean)
	if Library.disconnected then
		return
	end

	if input.KeyCode ~= Enum.KeyCode.Insert then
		return
	end

	if Library.scale_cooldown then
		return
	end

	if event then
		return
	end

	Library.open = not Library.open
	Library.can_be_optimized = not Library.open

	Library.normalize_size()
end)

function Library:__init__()
	local old_ui = self.parent:FindFirstChild(self.name)

	if old_ui then
		Debris:AddItem(old_ui, 0)
	end

	local Nurysium = Instance.new("ScreenGui")
	local Background = Instance.new("Frame")
	local UICorner = Instance.new("UICorner")
	local UIScale = Instance.new("UIScale")
	local Tabs = Instance.new("ScrollingFrame")
	local UIListLayout = Instance.new("UIListLayout")

	local optimized_folder = Instance.new('Folder', Nurysium)
	optimized_folder.Name = 'Optimized'

	local Sections_folder = Instance.new("Folder")

	Nurysium.Name = self.name
	Nurysium.Parent = self.parent
	Nurysium.ZIndexBehavior = Enum.ZIndexBehavior.Sibling

	Background.Name = "Background"
	Background.Parent = Nurysium
	Background.Active = true
	Background.AnchorPoint = Vector2.new(0.5, 0.5)
	Background.BackgroundColor3 = Color3.fromRGB(13, 13, 13)
	Background.BackgroundTransparency = 0.045
	Background.BorderColor3 = Color3.fromRGB(0, 0, 0)
	Background.BorderSizePixel = 0
	Background.Position = UDim2.new(0.5, 0, 0.5, 0)
	Background.Size = UDim2.new(0, 640, 0, 355)

	UICorner.CornerRadius = UDim.new(0, 10)
	UICorner.Parent = Background

	UIScale.Parent = Background
	UIScale.Scale = 0.01

	Tabs.Name = "Tabs"
	Tabs.Parent = Background
	Tabs.Active = true
	Tabs.AnchorPoint = Vector2.new(0.5, 0.5)
	Tabs.BackgroundColor3 = Color3.fromRGB(162, 162, 162)
	Tabs.BackgroundTransparency = 1.000
	Tabs.BorderColor3 = Color3.fromRGB(255, 255, 255)
	Tabs.BorderSizePixel = 0
	Tabs.Position = UDim2.new(0.132933617, 0, 0.498037577, 0)
	Tabs.Size = UDim2.new(0, 138, 0, 308)
	Tabs.ScrollBarImageColor3 = Color3.fromRGB(0, 0, 0)
	Tabs.BottomImage = ""
	Tabs.MidImage = ""
	Tabs.ScrollBarThickness = 0
	Tabs.TopImage = ""
	Tabs.CanvasSize = UDim2.new(0, 0, 0, 0)

	UIListLayout.Parent = Tabs
	UIListLayout.HorizontalAlignment = Enum.HorizontalAlignment.Center
	UIListLayout.SortOrder = Enum.SortOrder.LayoutOrder
	UIListLayout.Padding = UDim.new(0, 6)

	Sections_folder.Name = 'Sections'
	Sections_folder.Parent = Background

	Library.ui = Nurysium

	Library.changed(function()
		table.clear(Library.flags)

		Connections.abadone()

		Library.disconnected = true
	end)

	Library.normalize_size()

	Connections['ui_render'] = workspace.CurrentCamera:GetPropertyChangedSignal('ViewportSize'):Connect(function()
		Library.normalize_size()
	end)


	AcrylicBlur.new(Background)

	if Library.mobile then
		local MobileUI = Instance.new("ScreenGui")
		local Mobile = Instance.new("Frame")
		local MobileButton = Instance.new("TextButton")
		local UICorner = Instance.new("UICorner")
		local Icon = Instance.new("ImageLabel")
		local UIScale = Instance.new("UIScale")
		
		MobileUI.Name = "MobileUI"
		MobileUI.Parent = Nurysium
		MobileUI.ZIndexBehavior = Enum.ZIndexBehavior.Sibling
		
		Mobile.Name = "Mobile"
		Mobile.Parent = MobileUI
		Mobile.AnchorPoint = Vector2.new(0.5, 0.5)
		Mobile.BackgroundColor3 = Color3.fromRGB(255, 255, 255)
		Mobile.BackgroundTransparency = 1.000
		Mobile.BorderColor3 = Color3.fromRGB(0, 0, 0)
		Mobile.BorderSizePixel = 0
		Mobile.Position = UDim2.new(0.0620736592, 0, 0.926020384, 0)
		Mobile.Size = UDim2.new(0, 85, 0, 45)
		
		MobileButton.Name = "MobileButton"
		MobileButton.Parent = Mobile
		MobileButton.BackgroundColor3 = Color3.fromRGB(13, 13, 13)
		MobileButton.BackgroundTransparency = 0.015
		MobileButton.BorderColor3 = Color3.fromRGB(0, 0, 0)
		MobileButton.BorderSizePixel = 0
		MobileButton.Position = UDim2.new(0.243122876, 0, 0.171316162, 0)
		MobileButton.Size = UDim2.new(0, 43, 0, 28)
		MobileButton.Text = ""
		
		UICorner.CornerRadius = UDim.new(0, 6)
		UICorner.Parent = MobileButton
		
		Icon.Name = "Icon"
		Icon.Parent = MobileButton
		Icon.BackgroundColor3 = Color3.fromRGB(255, 255, 255)
		Icon.BackgroundTransparency = 1.000
		Icon.BorderColor3 = Color3.fromRGB(0, 0, 0)
		Icon.BorderSizePixel = 0
		Icon.Position = UDim2.new(0.303248554, 0, 0.214285716, 0)
		Icon.Size = UDim2.new(0, 15, 0, 15)
		Icon.Image = "rbxassetid://134992015790041"
		
		UIScale.Parent = Mobile
		UIScale.Scale = 1.340
	
		MobileButton.TouchTap:Connect(function()
			Library.open = not Library.open
			Library.can_be_optimized = not Library.open

			Library.normalize_size()
		end)
	end

	local TabsController = {}

	function TabsController.create_tab(text: string, image: string)
		local Tab = Instance.new("TextButton")
		local Title = Instance.new("TextLabel")
		local UICorner = Instance.new("UICorner")
		local Icon = Instance.new("ImageLabel")

		Tab.AutoButtonColor = false
		Tab.Name = math.random()
		Tab.Parent = Library.ui.Background.Tabs
		Tab.BackgroundColor3 = Color3.fromRGB(118, 124, 148)
		Tab.BackgroundTransparency = 1
		Tab.BorderColor3 = Color3.fromRGB(0, 0, 0)
		Tab.BorderSizePixel = 0
		Tab.Position = UDim2.new(0.0241379309, 0, 0, 0)
		Tab.Size = UDim2.new(0, 138, 0, 27)
		Tab.Text = ''
		Tab.TextColor3 = Color3.fromRGB(0, 0, 0)
		Tab.TextSize = 1.000
		Tab.TextWrapped = true

		local Right = Instance.new("ScrollingFrame")
		local Right_UIListLayout = Instance.new("UIListLayout")
		local Left = Instance.new("ScrollingFrame")
		local Left_UIListLayout_2 = Instance.new("UIListLayout")

		Right.Name = "Right"
		Right.Parent = Library.ui.Background.Sections
		Right.Active = true
		Right.AnchorPoint = Vector2.new(0.5, 0.5)
		Right.BackgroundColor3 = Color3.fromRGB(162, 162, 162)
		Right.BackgroundTransparency = 1
		Right.BorderColor3 = Color3.fromRGB(255, 255, 255)
		Right.BorderSizePixel = 0
		Right.Position = UDim2.new(0.794181466, 0, 0.497150093, 0)
		Right.Size = UDim2.new(0, 208, 0, 308)
		Right.ScrollBarImageColor3 = Color3.fromRGB(0, 0, 0)
		Right.BottomImage = ""
		Right.MidImage = ""
		Right.ScrollBarThickness = 0
		Right.TopImage = ""
		Right.Visible = false

		Right_UIListLayout.Parent = Right
		Right_UIListLayout.HorizontalAlignment = Enum.HorizontalAlignment.Center
		Right_UIListLayout.SortOrder = Enum.SortOrder.LayoutOrder
		Right_UIListLayout.Padding = UDim.new(0, 6)

		Left.Name = "Left"
		Left.Parent = Library.ui.Background.Sections
		Left.Active = true
		Left.AnchorPoint = Vector2.new(0.5, 0.5)
		Left.BackgroundColor3 = Color3.fromRGB(162, 162, 162)
		Left.BackgroundTransparency = 1
		Left.BorderColor3 = Color3.fromRGB(255, 255, 255)
		Left.BorderSizePixel = 0
		Left.Position = UDim2.new(0.44886893, 0, 0.497150093, 0)
		Left.Size = UDim2.new(0, 208, 0, 308)
		Left.ScrollBarImageColor3 = Color3.fromRGB(0, 0, 0)
		Left.BottomImage = ""
		Left.MidImage = ""
		Left.ScrollBarThickness = 0
		Left.TopImage = ""
		Left.Visible = false

		Left_UIListLayout_2.Parent = Left
		Left_UIListLayout_2.HorizontalAlignment = Enum.HorizontalAlignment.Center
		Left_UIListLayout_2.SortOrder = Enum.SortOrder.LayoutOrder
		Left_UIListLayout_2.Padding = UDim.new(0, 6)

		Tab.MouseButton1Up:Connect(function()
			if Library.current_tab == Tab.Name then
				return
			end

			Library.current_tab = Tab.Name

			UIManager.refresh_tabs(Tab)
			UIManager.refresh_sections(Right, Left)
			UIManager.animate_sections(Right, Left)
		end)

		Tab.TouchTap:Connect(function()
			if Library.current_tab == Tab.Name then
				return
			end

			Library.current_tab = Tab.Name

			UIManager.refresh_tabs(Tab)
			UIManager.refresh_sections(Right, Left)
			UIManager.animate_sections(Right, Left)
		end)


		Title.Name = "Title"
		Title.Parent = Tab
		Title.BackgroundColor3 = Color3.fromRGB(0, 0, 0)
		Title.BackgroundTransparency = 1.000
		Title.BorderColor3 = Color3.fromRGB(0, 0, 0)
		Title.BorderSizePixel = 0
		Title.Position = UDim2.new(0.3375673, 0, 0.277778059, 0)
		Title.Size = UDim2.new(0, 75, 0, 12)
		Title.FontFace = Font.new('rbxasset://fonts/families/GothamSSm.json', Enum.FontWeight.SemiBold, Enum.FontStyle.Normal)
		Title.Text = text
		Title.TextColor3 = Color3.fromRGB(255, 255, 255)
		Title.TextScaled = true
		Title.TextSize = 14.000
		Title.TextTransparency = 0.8
		Title.TextWrapped = true
		Title.TextXAlignment = Enum.TextXAlignment.Left

		UICorner.CornerRadius = UDim.new(0, 7.5)
		UICorner.Parent = Tab

		Icon.Name = "Icon"
		Icon.Parent = Tab
		Icon.BackgroundColor3 = Color3.fromRGB(255, 255, 255)
		Icon.BackgroundTransparency = 1.000
		Icon.BorderColor3 = Color3.fromRGB(0, 0, 0)
		Icon.BorderSizePixel = 0
		Icon.ImageTransparency = 0.8
		Icon.Position = UDim2.new(0.100000001, 0, 0.222222224, 0)
		Icon.Size = UDim2.new(0, 15, 0, 15)
		Icon.Image = image

		local ModuleController = {}

		function ModuleController:create_module(callback)
			local Module = Instance.new("Frame")
			local UICorner = Instance.new("UICorner")
			local Tab = Instance.new("TextButton")
			local Title = Instance.new("TextLabel")
			local UICorner_2 = Instance.new("UICorner")

			local AutomaticSize_fixer = Instance.new('Frame')
			local UICorner_fixer = Instance.new('Frame')

			Module.Name = "Module"
			Module.Parent = (self.side == 'right' and Right or Left)
			Module.Active = true
			Module.BackgroundColor3 = Color3.fromRGB(0, 0, 0)
			Module.BorderColor3 = Color3.fromRGB(0, 0, 0)
			Module.BorderSizePixel = 0
			Module.BackgroundTransparency = 1
			Module.Position = UDim2.new(0.0480769239, 0, 0, 0)
			Module.Selectable = true
			Module.Size = UDim2.new(0, 188, 0, 26)
			Module.AutomaticSize = Enum.AutomaticSize.Y

			UICorner.CornerRadius = UDim.new(0, 6)
			UICorner.Parent = Module

			Tab.Name = "Tab"
			Tab.Parent = Module
			Tab.BackgroundColor3 = Color3.fromRGB(34, 35, 39)
			Tab.BorderColor3 = Color3.fromRGB(0, 0, 0)
			Tab.BorderSizePixel = 0
			Tab.BackgroundTransparency = 1
			Tab.Size = UDim2.new(0, 188, 0, 26)
			Tab.AutoButtonColor = false
			Tab.Text = ''
			Tab.TextColor3 = Color3.fromRGB(0, 0, 0)
			Tab.TextSize = 1.000
			Tab.TextTransparency = 1.000
			Tab.TextWrapped = true

			Title.Name = "Title"
			Title.Parent = Tab
			Title.BackgroundColor3 = Color3.fromRGB(0, 0, 0)
			Title.BackgroundTransparency = 1.000
			Title.BorderColor3 = Color3.fromRGB(0, 0, 0)
			Title.BorderSizePixel = 0
			Title.Position = UDim2.new(0.0716098249, 0, 0.277777791, 0)
			Title.Size = UDim2.new(0, 140, 0, 12)
			Title.FontFace = Font.new('rbxasset://fonts/families/GothamSSm.json', Enum.FontWeight.SemiBold, Enum.FontStyle.Normal)
			Title.Text = self.text
			Title.TextColor3 = Color3.fromRGB(255, 255, 255)
			Title.TextScaled = true
			Title.TextSize = 14
			Title.TextWrapped = true
			Title.TextXAlignment = Enum.TextXAlignment.Left
			Title.TextTransparency = 0.7

			UICorner_2.CornerRadius = UDim.new(0, 5)
			UICorner_2.Parent = Tab

			UICorner_fixer.Parent = Tab
			UICorner_fixer.Size = UDim2.new(0, 188, 0, 4)
			UICorner_fixer.Position = UDim2.new(0, 0, 0.87, 0)
			UICorner_fixer.BackgroundColor3 = Color3.fromRGB(34, 35, 39)
			UICorner_fixer.ZIndex = 1
			UICorner_fixer.BorderColor3 = Color3.fromRGB(0, 0, 0)
			UICorner_fixer.BorderSizePixel = 0
			UICorner_fixer.BackgroundTransparency = 1

			local Settings = Instance.new("Frame")
			local UICorner = Instance.new("UICorner")
			local UIListLayout = Instance.new("UIListLayout")
			local UIPadding = Instance.new("UIPadding")

			Settings.Name = "Settings"
			Settings.Parent = Module
			Settings.Active = true
			Settings.BackgroundColor3 = Color3.fromRGB(15, 15, 15)
			Settings.BorderColor3 = Color3.fromRGB(0, 0, 0)
			Settings.BackgroundTransparency = 0.35
			Settings.BorderSizePixel = 0
			Settings.Selectable = true
			Settings.Size = UDim2.new(0, 188, 0, 0)
			Settings.ZIndex = 0
			Settings.AutomaticSize = Enum.AutomaticSize.Y

			UICorner.CornerRadius = UDim.new(0, 5)
			UICorner.Parent = Settings

			UIListLayout.Parent = Settings
			UIListLayout.HorizontalAlignment = Enum.HorizontalAlignment.Center
			UIListLayout.SortOrder = Enum.SortOrder.LayoutOrder
			UIListLayout.Padding = UDim.new(0, 6)

			UIPadding.Parent = Settings
			UIPadding.PaddingTop = UDim.new(0, 31)

			AutomaticSize_fixer.Parent = Settings
			AutomaticSize_fixer.LayoutOrder = 2147483647
			AutomaticSize_fixer.Size = UDim2.new(0, 0, 0, 0)
			AutomaticSize_fixer.BackgroundTransparency = 1

			local function update_module(switch: boolean)
				if switch then
					Library.flags[self.flag] = not Library.flags[self.flag]
				end

				callback(Library.flags[self.flag])

				if Library.flags[self.flag] then
					TweenService:Create(Tab, TweenInfo.new(1.2, Enum.EasingStyle.Exponential), {
						BackgroundTransparency = 0
					}):Play()

					TweenService:Create(UICorner_fixer, TweenInfo.new(1.2, Enum.EasingStyle.Exponential, Enum.EasingDirection.Out), {
						BackgroundTransparency = 0,
						BackgroundColor3 = Color3.fromRGB(34, 35, 39)
					}):Play()

					TweenService:Create(Title, TweenInfo.new(0.7, Enum.EasingStyle.Exponential), {
						TextTransparency = 0
					}):Play()
				else
					TweenService:Create(Tab, TweenInfo.new(0.7, Enum.EasingStyle.Exponential), {
						BackgroundTransparency = 1
					}):Play()

					TweenService:Create(UICorner_fixer, TweenInfo.new(0.7, Enum.EasingStyle.Exponential), {
						BackgroundTransparency = 1
					}):Play()

					TweenService:Create(Title, TweenInfo.new(0.7, Enum.EasingStyle.Exponential), {
						TextTransparency = 0.7
					}):Play()
				end
			end

			if not Library.flags[self.flag] then
				Library.flags[self.flag] = false
			else
				update_module(false)
			end

			Tab.MouseButton1Click:Connect(function()
				update_module(true)

				ConfigsController.save(game.GameId, Library.flags)
			end)

			Tab.TouchTap:Connect(function()
				update_module(true)

				ConfigsController.save(game.GameId, Library.flags)
			end)

			local SettingsController = {}

			function SettingsController:create_toggle()
				local Toggle = Instance.new("TextButton")
				local Title = Instance.new("TextLabel")
				local UICorner = Instance.new("UICorner")
				local ToggleFrame = Instance.new("Frame")
				local UICorner_2 = Instance.new("UICorner")

				Toggle.Name = "Toggle"
				Toggle.Parent = Settings
				Toggle.BackgroundColor3 = Color3.fromRGB(24, 24, 24)
				Toggle.BackgroundTransparency = 1
				Toggle.BorderColor3 = Color3.fromRGB(0, 0, 0)
				Toggle.BorderSizePixel = 0
				Toggle.Position = UDim2.new(0.0719927624, 0, 0, 0)
				Toggle.Size = UDim2.new(0, 174, 0, 20)
				Toggle.AutoButtonColor = false
				Toggle.Text = ""
				Toggle.TextColor3 = Color3.fromRGB(0, 0, 0)
				Toggle.TextSize = 1.000
				Toggle.TextTransparency = 1.000
				Toggle.TextWrapped = true

				Title.Name = "Title"
				Title.Parent = Toggle
				Title.BackgroundColor3 = Color3.fromRGB(0, 0, 0)
				Title.BackgroundTransparency = 1.000
				Title.BorderColor3 = Color3.fromRGB(0, 0, 0)
				Title.BorderSizePixel = 0
				Title.Position = UDim2.new(0.0400152095, 0, 0.1277771, 0)
				Title.Size = UDim2.new(0, 120, 0, 14)
				Title.FontFace = Font.new('rbxasset://fonts/families/GothamSSm.json', Enum.FontWeight.Medium, Enum.FontStyle.Normal)
				Title.Text = self.title
				Title.TextColor3 = Color3.fromRGB(255, 255, 255)
				Title.TextSize = 13.000
				Title.TextTransparency = 0.6
				Title.TextWrapped = true
				Title.TextXAlignment = Enum.TextXAlignment.Left

				UICorner.CornerRadius = UDim.new(0, 4)
				UICorner.Parent = Toggle

				ToggleFrame.Name = "ToggleFrame"
				ToggleFrame.Parent = Toggle
				ToggleFrame.Active = true
				ToggleFrame.BackgroundColor3 = Color3.fromRGB(14, 14, 14)
				ToggleFrame.BackgroundTransparency = 0.450
				ToggleFrame.BorderColor3 = Color3.fromRGB(0, 0, 0)
				ToggleFrame.BorderSizePixel = 0
				ToggleFrame.Position = UDim2.new(0.879999995, 0, 0.16, 0)
				ToggleFrame.Selectable = true
				ToggleFrame.Size = UDim2.new(0, 14, 0, 14)
				ToggleFrame.ZIndex = 0

				UICorner_2.CornerRadius = UDim.new(0, 5)
				UICorner_2.Parent = ToggleFrame

				local function update_toggle(switch: boolean)
					if switch then
						Library.flags[self.flag] = not Library.flags[self.flag]
					end

					if Library.flags[self.flag] then
						TweenService:Create(Title, TweenInfo.new(0.5, Enum.EasingStyle.Exponential), {
							TextTransparency = 0
						}):Play()

						TweenService:Create(Toggle, TweenInfo.new(1, Enum.EasingStyle.Exponential), {
							BackgroundTransparency = 0.1
						}):Play()

						TweenService:Create(ToggleFrame, TweenInfo.new(0.8, Enum.EasingStyle.Exponential), {
							BackgroundTransparency = 0,
							BackgroundColor3 = Color3.fromRGB(75, 75, 75)
						}):Play()
					else
						TweenService:Create(Title, TweenInfo.new(0.8, Enum.EasingStyle.Exponential), {
							TextTransparency = 0.6
						}):Play()

						TweenService:Create(Toggle, TweenInfo.new(0.8, Enum.EasingStyle.Exponential), {
							BackgroundTransparency = 1
						}):Play()

						TweenService:Create(ToggleFrame, TweenInfo.new(0.5, Enum.EasingStyle.Exponential), {
							BackgroundTransparency = 0.450,
							BackgroundColor3 = Color3.fromRGB(14, 14, 14)
						}):Play()
					end
				end

				if not Library.flags[self.flag] then
					Library.flags[self.flag] = false
				else
					update_toggle(false)
				end

				Toggle.MouseButton1Click:Connect(function()
					update_toggle(true)

					ConfigsController.save(game.GameId, Library.flags)
				end)

				Toggle.TouchTap:Connect(function()
					update_toggle(true)

					ConfigsController.save(game.GameId, Library.flags)
				end)
			end

			function SettingsController:create_slider()
				local Slider = Instance.new("Frame")
				local UICorner = Instance.new("UICorner")
				local Value = Instance.new("TextLabel")
				local Dragger = Instance.new("TextButton")
				local Hitbox = Instance.new("TextButton")
				local UICorner_2 = Instance.new("UICorner")

				Slider.Name = "Slider"
				Slider.Parent = Settings
				Slider.BackgroundColor3 = Color3.fromRGB(33, 33, 33)
				Slider.BorderColor3 = Color3.fromRGB(0, 0, 0)
				Slider.BorderSizePixel = 0
				Slider.BackgroundTransparency = 0.8
				Slider.Position = UDim2.new(0.0588235296, 0, 0.227272734, 0)
				Slider.Size = UDim2.new(0, 165, 0, 10)

				UICorner.CornerRadius = UDim.new(0, 6)
				UICorner.Parent = Slider

				Value.Name = "Value"
				Value.Parent = Slider
				Value.BackgroundColor3 = Color3.fromRGB(0, 0, 0)
				Value.BackgroundTransparency = 1.000
				Value.BorderColor3 = Color3.fromRGB(0, 0, 0)
				Value.BorderSizePixel = 0
				Value.Position = UDim2.new(0.380386621, 0, 0, 0.5)
				Value.Size = UDim2.new(0, 40, 0, 10)
				Value.ZIndex = 2
				Value.FontFace = Font.new('rbxasset://fonts/families/GothamSSm.json', Enum.FontWeight.SemiBold, Enum.FontStyle.Normal)
				Value.Text = self.value
				Value.TextColor3 = Color3.fromRGB(255, 255, 255)
				Value.TextScaled = false
				Value.TextSize = 8.000
				Value.TextWrapped = true

				Hitbox.Name = "Hitbox"
				Hitbox.Parent = Slider
				Hitbox.Active = false
				Hitbox.BackgroundTransparency = 1
				Hitbox.BorderColor3 = Color3.fromRGB(0, 0, 0)
				Hitbox.BorderSizePixel = 0
				Hitbox.Selectable = false
				Hitbox.ZIndex = 2
				Hitbox.Size = UDim2.new(1, 0, 0, 10)
				Hitbox.Text = ""

				Dragger.Name = "Dragger"
				Dragger.Parent = Slider
				Dragger.Active = false
				Dragger.BackgroundColor3 = Color3.fromRGB(50, 50, 50)
				Dragger.BorderColor3 = Color3.fromRGB(0, 0, 0)
				Dragger.BorderSizePixel = 0
				Dragger.Selectable = false
				Dragger.BackgroundTransparency = 0.7
				Dragger.Size = UDim2.new(self.value / 100, 0, 0, 10)
				Dragger.Text = ""

				UICorner_2.CornerRadius = UDim.new(0, 6)
				UICorner_2.Parent = Dragger

				local Title = Instance.new("TextLabel")

				Title.Name = "Title"
				Title.Parent = Settings
				Title.BackgroundColor3 = Color3.fromRGB(13, 13, 13)
				Title.BackgroundTransparency = 1.000
				Title.BorderColor3 = Color3.fromRGB(0, 0, 0)
				Title.BorderSizePixel = 0
				Title.Position = UDim2.new(0.0149251306, 0, 1.70000005, 0)
				Title.Size = UDim2.new(0, 162, 0, 8)
				Title.ZIndex = 3
				Title.FontFace = Font.new('rbxasset://fonts/families/GothamSSm.json', Enum.FontWeight.Medium, Enum.FontStyle.Normal)
				Title.Text = self.title
				Title.TextColor3 = Color3.fromRGB(255, 255, 255)
				Title.TextScaled = true
				Title.TextSize = 12.000
				Title.TextTransparency = 0.660
				Title.TextWrapped = true

				if not Library.flags[self.flag] then
					Library.flags[self.flag] = self.value
				else
					Value.Text = Library.flags[self.flag]
					Dragger.Size = UDim2.new(Library.flags[self.flag] / 100, 0, 0, 10)
				end

				local function update_slider()
					local output = math.clamp((mouse.X - Slider.AbsolutePosition.X) / Slider.AbsoluteSize.X, 0, 1)
					local value = math.round(output * 100)

					Library.flags[self.flag] = value
					Value.Text = math.round(output * 100)

					TweenService:Create(Dragger, TweenInfo.new(1, Enum.EasingStyle.Exponential), {
						Size = UDim2.new(output, 0, 0, 10)
					}):Play()

					ConfigsController.save(game.GameId, Library.flags)
				end
				local slider_active = false :: boolean

				local function activate_slider()
					slider_active = true

					while slider_active do
						update_slider()

						task.wait()
					end
				end

				Hitbox.MouseButton1Down:Connect(activate_slider)
				Hitbox.TouchLongPress:Connect(activate_slider)

				UserInputService.InputEnded:Connect(function(input)
					if input.UserInputType == Enum.UserInputType.MouseButton1 or input.UserInputType == Enum.UserInputType.Touch then
						slider_active = false
					end
				end)
			end
			
			function SettingsController:create_dropdown(callback)
				local Dropdown = Instance.new("Frame")
				local UICorner = Instance.new("UICorner")
				local ScrollingFrame = Instance.new("ScrollingFrame")
				local UIListLayout = Instance.new("UIListLayout")
				local UIPadding = Instance.new("UIPadding")
				local Title_2 = Instance.new("TextLabel")

				Dropdown.Name = "Dropdown"
				Dropdown.Parent = Settings
				Dropdown.BackgroundColor3 = Color3.fromRGB(13, 13, 13)
				Dropdown.BorderColor3 = Color3.fromRGB(0, 0, 0)
				Dropdown.BorderSizePixel = 0
                Dropdown.BackgroundTransparency = 0.5
				Dropdown.Position = UDim2.new(0.0588235296, 0, 0, 0)
				Dropdown.Size = UDim2.new(0, 165, 0, 80)

				UICorner.CornerRadius = UDim.new(0, 6)
				UICorner.Parent = Dropdown

				ScrollingFrame.Parent = Dropdown
				ScrollingFrame.Active = true
				ScrollingFrame.BackgroundColor3 = Color3.fromRGB(255, 255, 255)
				ScrollingFrame.BackgroundTransparency = 1.000
				ScrollingFrame.BorderColor3 = Color3.fromRGB(0, 0, 0)
				ScrollingFrame.BorderSizePixel = 0
				ScrollingFrame.Size = UDim2.new(1, 0, 1, 0)
				ScrollingFrame.ZIndex = 5
				ScrollingFrame.ScrollBarImageColor3 = Color3.fromRGB(60, 60, 60)
				ScrollingFrame.BottomImage = ""
				ScrollingFrame.ScrollBarThickness = 1
				ScrollingFrame.TopImage = ""
				ScrollingFrame.AutomaticCanvasSize = Enum.AutomaticSize.Y


				local function hide()
					if ScrollingFrame.Parent == optimized_folder then
						return
					end

					ScrollingFrame.Parent = optimized_folder
					ScrollingFrame.Visible = false
				end

				local function unhide()
					if ScrollingFrame.Parent == Dropdown then
						return
					end

					ScrollingFrame.Parent = Dropdown
					ScrollingFrame.Visible = true
				end

				Dropdown:GetPropertyChangedSignal('AbsoluteSize'):Connect(function()
					if Library.disconnected or #self.mods <= 20 then
						return
					end

					if not Library.can_be_optimized then
						task.delay(1, unhide)

						return
					end

					task.spawn(hide)
				end)

				UIListLayout.Parent = ScrollingFrame
				UIListLayout.HorizontalAlignment = Enum.HorizontalAlignment.Center
				UIListLayout.SortOrder = Enum.SortOrder.LayoutOrder
				UIListLayout.Padding = UDim.new(0, 6)

				local selected_mod
				
				local current_flag = Library.flags[self.flag]

				if not current_flag then
					Library.flags[self.flag] = self.default_flag or nil
					current_flag = Library.flags[self.flag]
				end

				for _, value in self.mods do
					local Mode = Instance.new("TextButton")
					local Title = Instance.new("TextLabel")
					local UICorner_2 = Instance.new("UICorner")

					Mode.Name = "Mode"
					Mode.Parent = ScrollingFrame
					Mode.BackgroundColor3 = Color3.fromRGB(24, 24, 24)
					Mode.BackgroundTransparency = 0.650
					Mode.BorderColor3 = Color3.fromRGB(0, 0, 0)
					Mode.BorderSizePixel = 0
					Mode.Size = UDim2.new(0, 144, 0, 22)
					Mode.AutoButtonColor = false
					Mode.Text = ""
					Mode.TextColor3 = Color3.fromRGB(0, 0, 0)
					Mode.TextSize = 1.000
					Mode.TextTransparency = 1.000
					Mode.TextWrapped = true

					Title.Name = "Title"
					Title.Parent = Mode
					Title.BackgroundColor3 = Color3.fromRGB(0, 0, 0)
					Title.BackgroundTransparency = 1.000
					Title.Size = UDim2.new(0, 122, 0, 12)
					Title.FontFace = Font.new('rbxasset://fonts/families/GothamSSm.json', Enum.FontWeight.Medium, Enum.FontStyle.Normal)
					Title.Text = value
					Title.Position = UDim2.new(0, 7, 0, 5)
					Title.TextColor3 = Color3.fromRGB(255, 255, 255)
					Title.TextScaled = true
					Title.TextSize = 14.000
					Title.TextTransparency = 0.300
					Title.TextWrapped = true
					Title.TextXAlignment = Enum.TextXAlignment.Left

					UICorner_2.CornerRadius = UDim.new(0, 6)
					UICorner_2.Parent = Mode
					
					if current_flag == value then
						selected_mod = Mode
						
						Mode.BackgroundColor3 = Color3.fromRGB(60, 60, 60)
					end

					Mode.MouseButton1Click:Connect(function()
						if selected_mod then
							TweenService:Create(selected_mod, TweenInfo.new(0.4, Enum.EasingStyle.Exponential), {
								BackgroundColor3 = Color3.fromRGB(24, 24, 24)
							}):Play()
						end

						selected_mod = Mode
						
						TweenService:Create(Mode, TweenInfo.new(1.2, Enum.EasingStyle.Exponential), {
							BackgroundColor3 = Color3.fromRGB(60, 60, 60)
						}):Play()
						
						Library.flags[self.flag] = value
						callback(Library.flags[self.flag])

						ConfigsController.save(game.GameId, Library.flags)
					end)

					Mode.TouchTap:Connect(function()
						if selected_mod then
							TweenService:Create(selected_mod, TweenInfo.new(0.4, Enum.EasingStyle.Exponential), {
								BackgroundColor3 = Color3.fromRGB(24, 24, 24)
							}):Play()
						end

						selected_mod = Mode
						
						TweenService:Create(Mode, TweenInfo.new(1.2, Enum.EasingStyle.Exponential), {
							BackgroundColor3 = Color3.fromRGB(60, 60, 60)
						}):Play()
						
						Library.flags[self.flag] = value
						callback(Library.flags[self.flag])

						ConfigsController.save(game.GameId, Library.flags)
					end)
				end

				UIPadding.Parent = ScrollingFrame
				UIPadding.PaddingTop = UDim.new(0, 10)

				Title_2.Name = "Title"
				Title_2.Parent = Settings
				Title_2.BackgroundColor3 = Color3.fromRGB(13, 13, 13)
				Title_2.BackgroundTransparency = 1.000
				Title_2.Size = UDim2.new(0, 144, 0, 8)
				Title_2.ZIndex = 2
				Title_2.FontFace = Font.new('rbxasset://fonts/families/GothamSSm.json', Enum.FontWeight.Medium, Enum.FontStyle.Normal)
				Title_2.Text = self.title
				Title_2.TextColor3 = Color3.fromRGB(255, 255, 255)
				Title_2.TextScaled = true
				Title_2.TextSize = 12.000
				Title_2.TextTransparency = 0.660
				Title_2.TextWrapped = true
			end

			return SettingsController
		end

		return ModuleController
	end

	return TabsController
end

return Library
