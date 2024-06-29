repeat
    task.wait()
until game:IsLoaded()

local ui = {
	background = Instance.new("Frame"),
	current_section = nil,
    is_loaded = false,
	is_open = false,
	UI_scale = 0,
	config = {}
}

local mouse = game:GetService('Players').LocalPlayer:GetMouse()
local lastMouseX = mouse.X

local ContextActionService = game:GetService('ContextActionService')
local TweenService = game:GetService('TweenService')
local HttpService = game:GetService('HttpService')
local RunService = game:GetService('RunService')

if not isfolder('Nury') then
	makefolder('Nury')
end

function ui.init_folders()
    local script_folder = isfolder('Nury/script')
    local data_folder = isfolder('Nury/DO-NOT-SHARE')
    local config_folder = isfolder('Nury/script/configs')

	if not script_folder then
        makefolder('Nury/script')
	end

    if not config_folder then
        makefolder('Nury/script/configs')
	end

    if not data_folder then
        makefolder('Nury/DO-NOT-SHARE')
    end
end

ui.init_folders()

function ui.save_config()
    if not ui.is_loaded then
        return
    end

	local config = HttpService:JSONEncode(ui.config)

	writefile('Nury/script/configs/default.txt', config)
end

function ui.load_config()
	if not isfile('Nury/script/configs/default.txt') then
        warn('FILE IS NULL')

		ui.save_config()

		return
	end

	local config_file = readfile('Nury/script/configs/default.txt')

	ui.config = HttpService:JSONDecode(config_file)
end


ui.load_config()

function get_mouse_direction()
	task.delay(0.01, function()
		lastMouseX = mouse.X
	end)
	
	return mouse.X - lastMouseX
end

local function animate_sections(speed: number)
	ui.background.sections.UIListLayout.Padding = UDim.new(1, 0)

	TweenService:Create(ui.background.sections.UIListLayout, TweenInfo.new(speed, Enum.EasingStyle.Exponential, Enum.EasingDirection.InOut), {
		Padding = UDim.new(0.0649999976, 0)
	}):Play()
end

local function animate_functions(speed: number)
	if not ui.current_section then
		return
	end
	
	ui.background.sections_background[ui.current_section].UIGridLayout.CellPadding = UDim2.new(0, 7, 0, -100)

	TweenService:Create(ui.background.sections_background[ui.current_section].UIGridLayout, TweenInfo.new(speed, Enum.EasingStyle.Exponential, Enum.EasingDirection.InOut), {
		CellPadding = UDim2.new(0, 7, 0, 7)
	}):Play()
end

function ui:get_screen_scale()
	local viewport_size_x = workspace.CurrentCamera.ViewportSize.X
	local viewport_size_y = workspace.CurrentCamera.ViewportSize.Y
	local screen_size = (viewport_size_x + viewport_size_y) / 3000

	ui.UI_scale = screen_size + math.max(0.85 - screen_size, 0)
end

function ui_open_close(self, input_state)
	if not self then
		return
	end
	
	if input_state ~= Enum.UserInputState.Begin then
		return
	end
	
	ui.is_open = not ui.is_open
	
	if not ui.is_open then
		animate_sections(1.3)
		
		if not ui.current_section then
			return
		end
		
		animate_functions(1)
	end
end

ContextActionService:BindAction('Gui', ui_open_close, true, Enum.KeyCode.RightShift)

task.delay(0.2, function()
	RunService:BindToRenderStep('render', 1, function()
		ui.get_screen_scale()

		if ui.is_open then
			TweenService:Create(ui.background.UIScale, TweenInfo.new(1, Enum.EasingStyle.Exponential), {
				
				Scale = 0
			}):Play()
		else
			TweenService:Create(ui.background.UIScale, TweenInfo.new(1.25, Enum.EasingStyle.Exponential), {
				Scale = ui.UI_scale
			}):Play()
		end
	end)
end)

function ui:init()
    ui.is_loaded = true

	local gui = Instance.new("ScreenGui")
	local UICorner = Instance.new("UICorner")
	local sections = Instance.new("ScrollingFrame")
	local UIListLayout = Instance.new("UIListLayout")
	local selection_box = Instance.new("Frame")
	local UICorner_2 = Instance.new("UICorner")
	local sections_background = Instance.new("Frame")
	local UICorner_3 = Instance.new("UICorner")
	local UIScale = Instance.new("UIScale")

	gui.Name = "gui"
	gui.Parent = self
	
	ui.background.Parent = gui

	ui.background.Name = "background"
	ui.background.Parent = gui
	ui.background.AnchorPoint = Vector2.new(0.5, 0.5)
	ui.background.BackgroundColor3 = Color3.fromRGB(10, 12, 13)
	ui.background.BorderColor3 = Color3.fromRGB(0, 0, 0)
	ui.background.BorderSizePixel = 0
	ui.background.Position = UDim2.new(0.499677122, 0, 0.499182284, 0)
	ui.background.Size = UDim2.new(0, 600, 0, 380)

	UICorner.CornerRadius = UDim.new(0.0350000001, 0)
	UICorner.Parent = ui.background

	sections.Name = "sections"
	sections.Parent = ui.background
	sections.Active = true
	sections.BackgroundColor3 = Color3.fromRGB(255, 255, 255)
	sections.BackgroundTransparency = 1.000
	sections.BorderColor3 = Color3.fromRGB(0, 0, 0)
	sections.BorderSizePixel = 0
	sections.Size = UDim2.new(0, 75, 0, 380)
	sections.ScrollBarImageColor3 = Color3.fromRGB(0, 0, 0)
	sections.CanvasSize = UDim2.new(0, 0, 0, 0)
	sections.ScrollBarThickness = 0

	UIListLayout.Parent = sections
	UIListLayout.HorizontalAlignment = Enum.HorizontalAlignment.Center
	UIListLayout.SortOrder = Enum.SortOrder.LayoutOrder
	UIListLayout.VerticalAlignment = Enum.VerticalAlignment.Center
	UIListLayout.Padding = UDim.new(0.0649999976, 0)

	selection_box.Name = "selection_box"
	selection_box.Parent = ui.background
	selection_box.AnchorPoint = Vector2.new(0.5, 0.5)
	selection_box.BackgroundColor3 = Color3.fromRGB(227, 232, 233)
	selection_box.BackgroundTransparency = 0.880
	selection_box.BorderColor3 = Color3.fromRGB(0, 0, 0)
	selection_box.BorderSizePixel = 0
	selection_box.Position = UDim2.new(0.0610886626, 0, 0.499182284, 0)
	selection_box.Size = UDim2.new(0, 45, 0, 45)
	selection_box.Visible = false

	UICorner_2.CornerRadius = UDim.new(0.400000006, 0)
	UICorner_2.Parent = selection_box

	sections_background.Name = "sections_background"
	sections_background.Parent = ui.background
	sections_background.AnchorPoint = Vector2.new(0.5, 0.5)
	sections_background.BackgroundColor3 = Color3.fromRGB(13, 14, 16)
	sections_background.BorderColor3 = Color3.fromRGB(0, 0, 0)
	sections_background.BorderSizePixel = 0
	sections_background.Position = UDim2.new(0.54397887, 0, 0.496983588, 0)
	sections_background.Size = UDim2.new(0, 501, 0, 327)

	UICorner_3.CornerRadius = UDim.new(0.0250000004, 0)
	UICorner_3.Parent = sections_background

	UIScale.Parent = ui.background
	UIScale.Scale = 10
	
	ui.background.MouseEnter:Connect(function()
		RunService:BindToRenderStep('position_update', 1, function()
			TweenService:Create(ui.background, TweenInfo.new(2, Enum.EasingStyle.Exponential, Enum.EasingDirection.Out), {
				Position = UDim2.new(0.499677122 + (-get_mouse_direction() / 10000), 0, 0.499182284, 0)
			}):Play()
		end)
	end)
	
	ui.background.MouseLeave:Connect(function()
		RunService:UnbindFromRenderStep('position_update')
	end)
end

function ui.create_section(data)
	local section = Instance.new("ImageButton")

	section.Name = data.name
	section.Parent = ui.background.sections
	section.BackgroundColor3 = Color3.fromRGB(255, 255, 255)
	section.BackgroundTransparency = 1.000
	section.BorderColor3 = Color3.fromRGB(0, 0, 0)
	section.BorderSizePixel = 0
	section.Position = UDim2.new(0.432142854, 0, 0.326829255, 0)
	section.Size = UDim2.new(0, 30, 0, 30)
	section.Image = `rbxassetid://{data.image}`
	section.ImageTransparency = 0.85
	
	local example = Instance.new("ScrollingFrame")
	local UIListLayout = Instance.new("UIListLayout")
	local UIGridLayout = Instance.new("UIGridLayout")

	example.Name = data.name
	example.Parent = ui.background.sections_background
	example.Active = true
	example.BackgroundColor3 = Color3.fromRGB(255, 255, 255)
	example.BackgroundTransparency = 1.000
	example.BorderColor3 = Color3.fromRGB(0, 0, 0)
	example.BorderSizePixel = 0
	example.Position = UDim2.new(0.0119760484, 0, 0.0336391442, 0)
	example.Size = UDim2.new(0, 488, 0, 306)
	example.ScrollBarImageColor3 = Color3.fromRGB(0, 0, 0)
	example.CanvasSize = UDim2.new(0, 0, 0, 0)
	example.ScrollBarThickness = 0
	example.Visible = false

	UIGridLayout.Parent = example
	UIGridLayout.HorizontalAlignment = Enum.HorizontalAlignment.Center
	UIGridLayout.SortOrder = Enum.SortOrder.LayoutOrder
	UIGridLayout.CellPadding = UDim2.new(0, 7, 0, 7)
	UIGridLayout.CellSize = UDim2.new(0, 235, 0, 65)

	--[[
	UIListLayout.Parent = example
	UIListLayout.Wraps = true
	UIListLayout.FillDirection = Enum.FillDirection.Horizontal
	UIListLayout.HorizontalAlignment = Enum.HorizontalAlignment.Center
	UIListLayout.Padding = UDim.new(0.0149999997, 0)
	]]
	
	section.MouseEnter:Connect(function()
		TweenService:Create(section, TweenInfo.new(0.35, Enum.EasingStyle.Exponential), {
			Size = UDim2.new(0, 35, 0, 35)
		}):Play()
	end)
	
	section.MouseLeave:Connect(function()
		TweenService:Create(section, TweenInfo.new(0.35, Enum.EasingStyle.Exponential), {
			Size = UDim2.new(0, 30, 0, 30)
		}):Play()
	end)
	
	section.MouseButton1Up:Connect(function()
		ui.current_section = section.Name
		
		animate_functions(1)
	end)
	
	RunService:BindToRenderStep('ui_update', 5, function()
		if ui.is_open then
			return
		end
		
		if not ui.current_section then
			return
		end
		
		TweenService:Create(section, TweenInfo.new(0.45, Enum.EasingStyle.Exponential), {
			ImageTransparency = (section.Name == ui.current_section and 0 or 0.85)
		}):Play()
		
		for __index, section in ui.background.sections_background:GetChildren() do
			if section:IsA('ScrollingFrame') then
				section.Visible = section.Name == ui.current_section
			end
		end
	end)
end

function ui.create_function(data, callback)
	callback = callback or function() end

	local toggled = false
	
	local Example = Instance.new("Frame")
	local UICorner = Instance.new("UICorner")
	local sorter = Instance.new("Frame")
	local UIListLayout = Instance.new("UIListLayout")
	local toggle = Instance.new("ImageLabel")
	local text = Instance.new("TextButton")
	local title = Instance.new("TextLabel")
	
	Example.Name = data.name
	Example.Parent = ui.background.sections_background:WaitForChild(data.section)
	Example.AnchorPoint = Vector2.new(0.5, 0.5)
	Example.BackgroundColor3 = Color3.fromRGB(10, 12, 13)
	Example.BorderColor3 = Color3.fromRGB(0, 0, 0)
	Example.BorderSizePixel = 0
	Example.Position = UDim2.new(0.256844193, 0, 0.0986940488, 0)
	Example.Size = UDim2.new(0, 230, 0, 60)

	UICorner.CornerRadius = UDim.new(0.100000001, 0)
	UICorner.Parent = Example

	sorter.Name = "sorter"
	sorter.Parent = Example
	sorter.BackgroundColor3 = Color3.fromRGB(255, 255, 255)
	sorter.BackgroundTransparency = 1.000
	sorter.BorderColor3 = Color3.fromRGB(0, 0, 0)
	sorter.BorderSizePixel = 0
	sorter.Position = UDim2.new(0.0651783794, 0, 0.448295087, 0)
	sorter.Size = UDim2.new(0, 200, 0, 25)

	UIListLayout.Parent = sorter
	UIListLayout.Padding = UDim.new(0.00999999978, 0)

	toggle.Name = "toggle"
	toggle.Parent = sorter
	toggle.BackgroundColor3 = Color3.fromRGB(255, 255, 255)
	toggle.BackgroundTransparency = 1.000
	toggle.BorderColor3 = Color3.fromRGB(0, 0, 0)
	toggle.BorderSizePixel = 0
	toggle.Position = UDim2.new(0.456521749, 0, 0.293157876, 0)
	toggle.Size = UDim2.new(0, 20, 0, 20)
	toggle.Image = "rbxassetid://18228140457"
	toggle.ImageTransparency = 0.800

	text.Name = "text"
	text.Parent = toggle
	text.BackgroundColor3 = Color3.fromRGB(255, 255, 255)
	text.BackgroundTransparency = 1.000
	text.BorderColor3 = Color3.fromRGB(0, 0, 0)
	text.BorderSizePixel = 0
	text.Position = UDim2.new(1.29999995, 0, 0.129999995, 0)
	text.Size = UDim2.new(0, 177, 0, 14)
	text.Font = Enum.Font.Gotham
	text.Text = "enabled"
	text.TextColor3 = Color3.fromRGB(134, 134, 134)
	text.TextSize = 14.000
	text.TextXAlignment = Enum.TextXAlignment.Left

	title.Name = "title"
	title.Parent = Example
	title.BackgroundColor3 = Color3.fromRGB(255, 255, 255)
	title.BackgroundTransparency = 1.000
	title.BorderColor3 = Color3.fromRGB(0, 0, 0)
	title.BorderSizePixel = 0
	title.Position = UDim2.new(0.0651782006, 0, 0.131926313, 0)
	title.Size = UDim2.new(0, 200, 0, 14)
	title.Font = Enum.Font.GothamMedium
	title.Text = data.name
	title.TextColor3 = Color3.fromRGB(255, 255, 255)
	title.TextScaled = true
	title.TextSize = 14.000
	title.TextWrapped = true
	title.TextXAlignment = Enum.TextXAlignment.Left

    for __index, element in ui.config do
        if element == data.name then
            toggled = true
		    callback(toggled)

            task.delay(0.15, function()
				toggle.Image = "rbxassetid://18229027207"
			end)

            TweenService:Create(toggle, TweenInfo.new(1.2, Enum.EasingStyle.Exponential), {
                ImageTransparency = 0.35
            }):Play()
        end
    end

	text.MouseButton1Up:Connect(function()
		toggled = not toggled
		callback(toggled)
		
		if toggled then
            table.insert(ui.config, data.name)

			task.delay(0.15, function()
				toggle.Image = "rbxassetid://18229027207"
			end)
			
			TweenService:Create(toggle, TweenInfo.new(0.1, Enum.EasingStyle.Exponential), {
				ImageTransparency = 0.9
			}):Play()

			task.delay(0.35, function()
				TweenService:Create(toggle, TweenInfo.new(0.2, Enum.EasingStyle.Exponential), {
					ImageTransparency = 0.35
				}):Play()
			end)
		else
            for index, element in ui.config do
                if element == data.name then
                    table.remove(ui.config, index)
                end
            end

			task.delay(0.15, function()
				toggle.Image = "rbxassetid://18228140457"
			end)
			
			TweenService:Create(toggle, TweenInfo.new(0.1, Enum.EasingStyle.Exponential), {
				ImageTransparency = 0.9
			}):Play()
			
			task.delay(0.35, function()
				TweenService:Create(toggle, TweenInfo.new(0.2, Enum.EasingStyle.Exponential), {
					ImageTransparency = 0.8
				}):Play()
			end)
		end

        ui.save_config()
	end)
end

return ui
