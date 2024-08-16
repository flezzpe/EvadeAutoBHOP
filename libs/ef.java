local Debris = cloneref(game:GetService('Debris'))
local Players = cloneref(game:GetService('Players'))
local RunService = cloneref(game:GetService('RunService'))
local TweenService = cloneref(game:GetService('TweenService'))
local UserInputService = cloneref(game:GetService('UserInputService'))
local ContextActionService = cloneref(game:GetService('ContextActionService'))

repeat
	RunService.PostSimulation:Wait(1)
until game:IsLoaded()

local localplayer = Players.LocalPlayer
local mouse = localplayer:GetMouse()

local nurysium_ui = {
	user_gui = Instance.new("ScreenGui")
}

local ui = {}

ui.UI_scale = 0
ui.open = false
ui.have_keyboard = UserInputService.KeyboardEnabled
ui.currect_category = nil

--// flags

ui.toggles = {}
ui.dropdowns = {}
ui.sliders = {}

function nurysium_ui:preload_assets_in_cache()
	local enabled_toggle = Instance.new('ImageButton', self.parent)
	enabled_toggle.Image = 'rbxassetid://18497520940'
	
	Debris:AddItem(enabled_toggle, 0.1)
end

function ui:set_screen_scale()
	local viewport_size_x = workspace.CurrentCamera.ViewportSize.X
	local viewport_size_y = workspace.CurrentCamera.ViewportSize.Y
	
	local screen_size = (viewport_size_x + viewport_size_y) / 3100

	if not ui.have_keyboard then
		screen_size = (viewport_size_x + viewport_size_y) / 4000
	end

	ui.UI_scale = screen_size + math.max(0.65 - screen_size, 0)
end

function ui_open_close(self, input_state)
	if not self then
		return
	end

	if input_state ~= Enum.UserInputState.Begin then
		return
	end

	ui.open = not ui.open

	if ui.open then
		animate_categorys(2)

		ui_perform_neon_animation()

		if not ui.currect_category then
			return
		end

		animate_sections(1.7)
	end
end

function animate_categorys(speed: number)
	nurysium_ui.user_gui.background.categorys.UIListLayout.Padding = UDim.new(1, 0)

	TweenService:Create(nurysium_ui.user_gui.background.categorys.UIListLayout, TweenInfo.new(speed, Enum.EasingStyle.Exponential, Enum.EasingDirection.InOut), {
		Padding = UDim.new(0.08, 0)
	}):Play()
end

function animate_sections(speed: number)
	nurysium_ui.user_gui.background.left.UIListLayout.Padding = UDim.new(0, 700)
	nurysium_ui.user_gui.background.right.UIListLayout.Padding = UDim.new(0, 700)

	TweenService:Create(nurysium_ui.user_gui.background.left.UIListLayout, TweenInfo.new(speed, Enum.EasingStyle.Exponential, Enum.EasingDirection.InOut), {
		Padding = UDim.new(0, 30)
	}):Play()
	
	TweenService:Create(nurysium_ui.user_gui.background.right.UIListLayout, TweenInfo.new(speed + 0.314159, Enum.EasingStyle.Exponential, Enum.EasingDirection.InOut), {
		Padding = UDim.new(0, 30)
	}):Play()

	task.defer(function()
		for _, uiscale in nurysium_ui.user_gui.background.left:GetDescendants() do
			if not uiscale:IsA('UIScale') then
				continue
			end

			if uiscale.Name ~= 'section_scaller' then
				continue
			end

			uiscale.Scale = 0

			TweenService:Create(uiscale, TweenInfo.new(speed / 2, Enum.EasingStyle.Back, Enum.EasingDirection.InOut), {
				Scale = 0.91
			}):Play()
		end
	end)

	task.defer(function()
		for _, uiscale in nurysium_ui.user_gui.background.right:GetDescendants() do
			if not uiscale:IsA('UIScale') then
				continue
			end

			if uiscale.Name ~= 'section_scaller' then
				continue
			end
		
			uiscale.Scale = 0

			TweenService:Create(uiscale, TweenInfo.new(speed / 1.5, Enum.EasingStyle.Back, Enum.EasingDirection.InOut), {
				Scale = 0.91
			}):Play()
		end
	end)
end

local on_animation = false :: boolean

function ui_perform_neon_animation()
	if on_animation then
		return
	end
	
	on_animation = true
	
	local user_gui = nurysium_ui.user_gui
	
	if not user_gui then
		return
	end
	
	local gradient = user_gui.background.upper_background:FindFirstChildOfClass('UIStroke'):FindFirstChildOfClass('UIGradient')
	
	if not gradient then
		return
	end
	
	TweenService:Create(gradient, TweenInfo.new(3, Enum.EasingStyle.Exponential), {
		Rotation = 90
	}):Play()
	
	task.delay(3, function()
		TweenService:Create(gradient, TweenInfo.new(2.5, Enum.EasingStyle.Quart), {
			Rotation = 180
		}):Play()
	end)
	
	task.delay(6, function()
		gradient.Rotation = -180
		on_animation = false
	end)
end

ContextActionService:BindAction('native gui', ui_open_close, false, Enum.KeyCode.RightShift)

task.delay(0.2, function()
    
    local connection 
    connection = RunService.RenderStepped:Connect(function()
		if not nurysium_ui.user_gui:FindFirstChild('background') then
			connection:Disconnect()

            return
		end

		if not nurysium_ui.user_gui.background:FindFirstChildOfClass('UIScale') then
			return
		end

		ui.set_screen_scale()

		TweenService:Create(nurysium_ui.user_gui.mobile_button.UIScale, TweenInfo.new(1, Enum.EasingStyle.Back), {
			Scale = ui.UI_scale / 1.35
		}):Play()

		if not ui.open then
			TweenService:Create(nurysium_ui.user_gui.background.UIScale, TweenInfo.new(0.45, Enum.EasingStyle.Back), {
				Scale = 0
			}):Play()
		else
			TweenService:Create(nurysium_ui.user_gui.background.UIScale, TweenInfo.new(0.75, Enum.EasingStyle.Back), {
				Scale = ui.UI_scale
			}):Play()
		end
	end)
end)

function nurysium_ui:__initializate()
	if not self then
		warn('[initialization erorr]: self is null. Forgot to set up the table?')
		
		return
	end

	local background = Instance.new("Frame")
	local left_line = Instance.new("Frame")
	local UIGradient = Instance.new("UIGradient")
	local categorys = Instance.new("ScrollingFrame")
	local UIListLayout = Instance.new("UIListLayout")
	local UIPadding = Instance.new("UIPadding")
	local left = Instance.new("ScrollingFrame")
	local UIListLayout_2 = Instance.new("UIListLayout")
	local UIPadding_2 = Instance.new("UIPadding")
	local UICorner = Instance.new("UICorner")
	local UIGradient_2 = Instance.new("UIGradient")
	local hub_name = Instance.new("TextLabel")
	local upper_background = Instance.new("Frame")
	local UICorner_2 = Instance.new("UICorner")
	local fixer = Instance.new("Frame")
	local UIGradient_3 = Instance.new("UIGradient")
	local UIStroke = Instance.new("UIStroke")
	local UIGradient_4 = Instance.new("UIGradient")
	local UIGradient_5 = Instance.new("UIGradient")
	local right = Instance.new("ScrollingFrame")
	local UIPadding_3 = Instance.new("UIPadding")
	local UIListLayout_3 = Instance.new("UIListLayout")
	local UIScale = Instance.new("UIScale")
	
	local user_gui = nurysium_ui.user_gui

    if self.parent:FindFirstChild('nurysium') then
        Debris:AddItem(self.parent.nurysium, 0)
    end

	user_gui.Name = "nurysium"
	user_gui.Parent = self.parent

	task.defer(function()
		local mobile_button = Instance.new("Frame")
		local UICorner = Instance.new("UICorner")
		local UIGradient = Instance.new("UIGradient")
		local Button = Instance.new("ImageButton")
		local UIScale = Instance.new('UIScale', mobile_button)

		mobile_button.Name = "mobile_button"
		mobile_button.Parent = user_gui
		mobile_button.AnchorPoint = Vector2.new(0.5, 0.5)
		mobile_button.BackgroundColor3 = Color3.fromRGB(255, 255, 255)
		mobile_button.BorderColor3 = Color3.fromRGB(0, 0, 0)
		mobile_button.BorderSizePixel = 0
		mobile_button.Position = UDim2.new(0.0650908053, 0, 0.931750059, 0)
		mobile_button.Size = UDim2.new(0, 88, 0, 55)

		UICorner.CornerRadius = UDim.new(0, 15)
		UICorner.Parent = mobile_button

		UIGradient.Color = ColorSequence.new{ColorSequenceKeypoint.new(0.00, Color3.fromRGB(16, 16, 19)), ColorSequenceKeypoint.new(1.00, Color3.fromRGB(16, 16, 16))}
		UIGradient.Rotation = -56
		UIGradient.Parent = mobile_button

		Button.Name = "Button"
		Button.Parent = mobile_button
		Button.BackgroundColor3 = Color3.fromRGB(255, 255, 255)
		Button.BackgroundTransparency = 1.000
        Button.ImageTransparency = 0.700
		Button.BorderColor3 = Color3.fromRGB(0, 0, 0)
		Button.BorderSizePixel = 0
		Button.Position = UDim2.new(0.284090906, 0, 0.163636357, 0)
		Button.Size = UDim2.new(0.420454532, 0, 0.672727287, 0)
		Button.Image = "rbxassetid://17615525476"

		Button.MouseButton1Up:Connect(function()
			ui.open = not ui.open

			if ui.open then
				animate_categorys(2)

				ui_perform_neon_animation()

				if not ui.currect_category then
					return
				end

				animate_sections(1.7)
			end
		end)

		Button.TouchTap:Connect(function()
			ui.open = not ui.open

			if ui.open then
				animate_categorys(2)

				ui_perform_neon_animation()

				if not ui.currect_category then
					return
				end

				animate_sections(1.7)
			end
		end)
	end)

	background.Name = "background"
	background.Parent = user_gui
	background.AnchorPoint = Vector2.new(0.5, 0.5)
	background.BackgroundColor3 = Color3.fromRGB(255, 255, 255)
	background.BorderColor3 = Color3.fromRGB(0, 0, 0)
	background.BorderSizePixel = 0
	background.Position = UDim2.new(0.499778897, 0, 0.520615399, 0)
	background.Size = UDim2.new(0, 660, 0, 410)

	left_line.Name = "left_line"
	left_line.Parent = background
	left_line.AnchorPoint = Vector2.new(0.5, 0.5)
	left_line.BackgroundColor3 = Color3.fromRGB(27, 27, 27)
	left_line.BackgroundTransparency = 0.400
	left_line.BorderColor3 = Color3.fromRGB(0, 0, 0)
	left_line.BorderSizePixel = 0
	left_line.Position = UDim2.new(0.260138303, 0, 0.499588966, 0)
	left_line.Size = UDim2.new(0, 1, 0, 368)

	UIGradient.Rotation = 90
	UIGradient.Transparency = NumberSequence.new{NumberSequenceKeypoint.new(0.00, 1.00), NumberSequenceKeypoint.new(0.50, 0.00), NumberSequenceKeypoint.new(1.00, 1.00)}
	UIGradient.Parent = left_line

	categorys.Name = "categorys"
	categorys.Parent = background
	categorys.Active = true
	categorys.BackgroundColor3 = Color3.fromRGB(255, 255, 255)
	categorys.BackgroundTransparency = 1.000
	categorys.BorderColor3 = Color3.fromRGB(0, 0, 0)
	categorys.BorderSizePixel = 0
	categorys.Position = UDim2.new(0, 0, 0.050808344, 0)
	categorys.Size = UDim2.new(0, 171, 0, 356)
	categorys.ScrollBarImageColor3 = Color3.fromRGB(0, 0, 0)
	categorys.CanvasSize = UDim2.new(0, 0, 0, 0)
	categorys.ScrollBarThickness = 0

	UIListLayout.Parent = categorys
	UIListLayout.HorizontalAlignment = Enum.HorizontalAlignment.Center
	UIListLayout.SortOrder = Enum.SortOrder.LayoutOrder
	UIListLayout.Padding = UDim.new(0.0799999982, 0)

	UIPadding.Parent = categorys
	UIPadding.PaddingTop = UDim.new(0.0299999993, 0)

	left.Name = "left"
	left.Parent = background
	left.Active = true
	left.AnchorPoint = Vector2.new(0.5, 0.5)
	left.BackgroundColor3 = Color3.fromRGB(255, 255, 255)
	left.BackgroundTransparency = 1.000
	left.BorderColor3 = Color3.fromRGB(0, 0, 0)
	left.BorderSizePixel = 0
	left.Position = UDim2.new(0.452314526, 0, 0.488017619, 0)
	left.Size = UDim2.new(0, 225, 0, 337)
	left.ZIndex = 6
	left.ScrollBarImageColor3 = Color3.fromRGB(0, 0, 0)
	left.CanvasSize = UDim2.new(0, 0, 50, 0)
	left.ScrollBarThickness = 0
    left.ScrollBarImageTransparency = 1

	UIListLayout_2.Parent = left
	UIListLayout_2.SortOrder = Enum.SortOrder.LayoutOrder
	UIListLayout_2.Padding = UDim.new(0, 30)

	UIPadding_2.Parent = left
	UIPadding_2.PaddingTop = UDim.new(0.00100000005, 0)

	UICorner.CornerRadius = UDim.new(0, 15)
	UICorner.Parent = background

	UIGradient_2.Color = ColorSequence.new{ColorSequenceKeypoint.new(0.00, Color3.fromRGB(16, 16, 19)), ColorSequenceKeypoint.new(1.00, Color3.fromRGB(16, 16, 16))}
	UIGradient_2.Rotation = -56
	UIGradient_2.Parent = background

	hub_name.Name = "hub_name"
	hub_name.Parent = background
	hub_name.BackgroundColor3 = Color3.fromRGB(255, 255, 255)
	hub_name.BackgroundTransparency = 1.000
	hub_name.BorderColor3 = Color3.fromRGB(0, 0, 0)
	hub_name.BorderSizePixel = 0
	hub_name.Position = UDim2.new(0.446609765, 0, -0.085, 0)
	hub_name.Size = UDim2.new(0, 70, 0, 21)
	hub_name.Font = Enum.Font.GothamMedium
	hub_name.Text = "nurysium"
	hub_name.TextColor3 = Color3.fromRGB(255, 255, 255)
	hub_name.TextScaled = true
	hub_name.TextSize = 14.000
	hub_name.TextWrapped = true

	upper_background.Name = "upper_background"
	upper_background.Parent = background
	upper_background.AnchorPoint = Vector2.new(0.5, 0.5)
	upper_background.BackgroundColor3 = Color3.fromRGB(255, 255, 255)
	upper_background.BackgroundTransparency = 0.250
	upper_background.BorderColor3 = Color3.fromRGB(0, 0, 0)
	upper_background.BorderSizePixel = 0
	upper_background.Position = UDim2.new(0.499778807, 0, -0.041175209, 0)
	upper_background.Size = UDim2.new(0, 660, 0, 60)
	upper_background.ZIndex = 0

	UICorner_2.CornerRadius = UDim.new(0, 15)
	UICorner_2.Parent = upper_background

	fixer.Name = "fixer"
	fixer.Parent = upper_background
	fixer.AnchorPoint = Vector2.new(0.5, 0.5)
	fixer.BackgroundColor3 = Color3.fromRGB(255, 255, 255)
	fixer.BorderColor3 = Color3.fromRGB(0, 0, 0)
	fixer.BorderSizePixel = 0
	fixer.Position = UDim2.new(0.5, 0, 0.861000001, 0)
	fixer.Size = UDim2.new(0, 660, 0, 15)
	fixer.ZIndex = 0

	UIGradient_3.Color = ColorSequence.new{ColorSequenceKeypoint.new(0.00, Color3.fromRGB(16, 16, 19)), ColorSequenceKeypoint.new(1.00, Color3.fromRGB(16, 16, 16))}
	UIGradient_3.Rotation = -56
	UIGradient_3.Parent = fixer

	UIStroke.Parent = upper_background
	UIStroke.Color = Color3.fromRGB(255, 255, 255)
	UIStroke.Thickness = 0.7

	UIGradient_4.Color = ColorSequence.new{ColorSequenceKeypoint.new(0.00, Color3.fromRGB(55, 97, 189)), ColorSequenceKeypoint.new(1.00, Color3.fromRGB(55, 97, 189))}
	UIGradient_4.Rotation = -180
	UIGradient_4.Transparency = NumberSequence.new{NumberSequenceKeypoint.new(0.00, 1.00), NumberSequenceKeypoint.new(0.40, 0.73), NumberSequenceKeypoint.new(0.50, 0.00), NumberSequenceKeypoint.new(0.60, 0.74), NumberSequenceKeypoint.new(1.00, 1.00)}
	UIGradient_4.Parent = UIStroke

	UIGradient_5.Color = ColorSequence.new{ColorSequenceKeypoint.new(0.00, Color3.fromRGB(16, 16, 19)), ColorSequenceKeypoint.new(1.00, Color3.fromRGB(16, 16, 16))}
	UIGradient_5.Rotation = -90
	UIGradient_5.Parent = upper_background

	right.Name = "right"
	right.Parent = background
	right.Active = true
	right.AnchorPoint = Vector2.new(0.5, 0.5)
	right.BackgroundColor3 = Color3.fromRGB(255, 255, 255)
	right.BackgroundTransparency = 1.000
	right.BorderColor3 = Color3.fromRGB(0, 0, 0)
	right.BorderSizePixel = 0
	right.Position = UDim2.new(0.80382967, 0, 0.485578597, 0)
	right.Size = UDim2.new(0, 225, 0, 337)
	right.ZIndex = 6
	right.ScrollBarImageColor3 = Color3.fromRGB(0, 0, 0)
	right.CanvasSize = UDim2.new(0, 0, 50, 0)
	right.ScrollBarThickness = 0
    right.ScrollBarImageTransparency = 1

	UIPadding_3.Parent = right
	UIPadding_3.PaddingTop = UDim.new(0.00100000005, 0)

	UIListLayout_3.Parent = right
	UIListLayout_3.SortOrder = Enum.SortOrder.LayoutOrder
	UIListLayout_3.Padding = UDim.new(0, 30)

	UIScale.Parent = background
	UIScale.Scale = 0
end

function nurysium_ui:create_category()
	if not self then
		warn('[create category erorr]: self is null. Forgot to set up the table?')
		
		return
	end
	
	local Example = Instance.new("TextButton")
	local hover = Instance.new("Frame")
	local UICorner = Instance.new("UICorner")
	local UIGradient = Instance.new("UIGradient")
	local icon = Instance.new("ImageLabel")
	local noise_shader = Instance.new("ImageLabel")
	local UICorner_2 = Instance.new("UICorner")

	Example.Name = self.name
	Example.Parent = nurysium_ui.user_gui.background.categorys
	Example.BackgroundColor3 = Color3.fromRGB(255, 255, 255)
	Example.BackgroundTransparency = 1.000
	Example.BorderColor3 = Color3.fromRGB(0, 0, 0)
	Example.BorderSizePixel = 0
	Example.Position = UDim2.new(0.248538017, 0, -1.07706761e-07, 0)
	Example.Size = UDim2.new(0, 96, 0, 20)
	Example.Font = Enum.Font.GothamMedium
	Example.TextColor3 = Color3.fromRGB(195, 195, 195)
	Example.TextScaled = false
	Example.TextSize = 17
	Example.TextTransparency = 0.45
	Example.TextWrapped = true
	Example.TextXAlignment = Enum.TextXAlignment.Left
	Example.Text = self.name

	hover.Name = "hover"
	hover.Parent = Example
	hover.BackgroundColor3 = Color3.fromRGB(167, 167, 167)
	hover.BorderColor3 = Color3.fromRGB(0, 0, 0)
	hover.BorderSizePixel = 0
	hover.Position = UDim2.new(-0.128430679, 0, -0.485668957, 0)
	hover.Size = UDim2.new(0, 120, 0, 39)
	hover.ZIndex = 2

	UICorner.CornerRadius = UDim.new(0, 6)
	UICorner.Parent = hover

	UIGradient.Color = ColorSequence.new{ColorSequenceKeypoint.new(0.00, Color3.fromRGB(19, 19, 21)), ColorSequenceKeypoint.new(1.00, Color3.fromRGB(255, 255, 255))}
	UIGradient.Transparency = NumberSequence.new{NumberSequenceKeypoint.new(0.00, 0.86), NumberSequenceKeypoint.new(1.00, 0.95)}
	UIGradient.Parent = hover

	icon.Name = "icon"
	icon.Parent = hover
	icon.BackgroundColor3 = Color3.fromRGB(255, 255, 255)
	icon.BackgroundTransparency = 1.000
	icon.BorderColor3 = Color3.fromRGB(0, 0, 0)
	icon.ImageTransparency = 0.65
	icon.BorderSizePixel = 0
	icon.Position = UDim2.new(0.735000014, 0, 0.274702013, 0)
	icon.Size = UDim2.new(0, 18, 0, 18)
	icon.Image = "rbxassetid://" .. self.image_id
	icon.ImageColor3 = Color3.fromRGB(255, 248, 247)

	noise_shader.Name = "noise_shader"
	noise_shader.Parent = hover
	noise_shader.BackgroundColor3 = Color3.fromRGB(255, 255, 255)
	noise_shader.BackgroundTransparency = 1.000
	noise_shader.BorderColor3 = Color3.fromRGB(0, 0, 0)
	noise_shader.BorderSizePixel = 0
	noise_shader.Size = UDim2.new(1, 0, 1, 0)
	noise_shader.Image = "rbxassetid://18920632099"
	noise_shader.ImageTransparency = 1

	UICorner_2.CornerRadius = UDim.new(0, 15)
	UICorner_2.Parent = noise_shader

	Example.MouseButton1Up:Connect(function()
		if ui.currect_category == self.name then
			return
		end
		
		animate_sections(1.7)

		ui.currect_category = self.name
	end)

	Example.TouchTap:Connect(function()
		if ui.currect_category == self.name then
			return
		end

		animate_sections(1.7)

		ui.currect_category = self.name
	end)

	local connection
	connection = RunService.Heartbeat:Connect(function()
		if nurysium_ui.user_gui.background == nil then
			connection:Disconnect()
			warn('tonka')

            		return
		end

		TweenService:Create(icon, TweenInfo.new(0.45, Enum.EasingStyle.Exponential), {
			ImageColor3 = (self.name == ui.currect_category and Color3.fromRGB(55, 97, 189) or Color3.fromRGB(255, 248, 247)),
			ImageTransparency = (self.name == ui.currect_category and 0 or 0.65)
		}):Play()
	
		TweenService:Create(Example, TweenInfo.new(2, Enum.EasingStyle.Exponential), {
			TextTransparency = (self.name == ui.currect_category and 0.1 or 0.45)
		}):Play()
		
		TweenService:Create(noise_shader, TweenInfo.new(0.4, Enum.EasingStyle.Exponential), {
			ImageTransparency = (self.name == ui.currect_category and 0.314 or 1)
		}):Play()
		
		TweenService:Create(hover, TweenInfo.new(1, Enum.EasingStyle.Exponential), {
			BackgroundTransparency = (self.name == ui.currect_category and 0 or 1),
			BackgroundColor3 = (self.name == ui.currect_category and Color3.fromRGB(55, 97, 189) or Color3.fromRGB(167, 167, 167))
		}):Play()
	end)
end

function nurysium_ui:create_section()
	local example = Instance.new("Frame")
	local frame_UIScale = Instance.new('UIScale', example)
	local UICorner = Instance.new("UICorner")
	local UIListLayout = Instance.new("UIListLayout")
	local UIPadding = Instance.new("UIPadding")
	local container = Instance.new("Frame")
	local container_2 = Instance.new("Frame")
	local name = Instance.new("TextLabel")
	local fixer = Instance.new('Frame', example)

	frame_UIScale.Name = 'section_scaller'

	fixer.Name = 'fixer'
	fixer.BackgroundTransparency = 1
	fixer.Size = UDim2.new(0.5, 205, 0, 0.1)
	fixer.LayoutOrder = 99999

	example.Name = self.name
	example.Parent = nurysium_ui.user_gui.background:FindFirstChild(self.side:lower())
	example.BackgroundColor3 = Color3.fromRGB(18, 18, 18)
	example.BorderColor3 = Color3.fromRGB(0, 0, 0)
	example.BorderSizePixel = 0
	example.Size = UDim2.new(0, 205, 0, 21)
	example.AutomaticSize = Enum.AutomaticSize.Y

	UICorner.CornerRadius = UDim.new(0, 10)
	UICorner.Parent = example

	UIListLayout.Parent = example
	UIListLayout.SortOrder = Enum.SortOrder.LayoutOrder
	UIListLayout.VerticalAlignment = Enum.VerticalAlignment.Center
	UIListLayout.Padding = UDim.new(0, 4)

	UIPadding.Parent = example
	UIPadding.PaddingLeft = UDim.new(0.0500000007, 0)

	container.Name = "container"
	container.Parent = example
	container.AnchorPoint = Vector2.new(0.5, 0.5)
	container.BackgroundColor3 = Color3.fromRGB(17, 2, 11)
	container.BackgroundTransparency = 1.000
	container.BorderColor3 = Color3.fromRGB(0, 0, 0)
	container.BorderSizePixel = 0
	container.Position = UDim2.new(0.477588981, 0, 0.484575212, 0)
	container.Size = UDim2.new(0, 187, 0, -0.5)
	container.ZIndex = 6

	container_2.Name = "container"
	container_2.Parent = container
	container_2.BackgroundColor3 = Color3.fromRGB(17, 2, 11)
	container_2.BackgroundTransparency = 1.000
	container_2.BorderColor3 = Color3.fromRGB(0, 0, 0)
	container_2.BorderSizePixel = 0
	container_2.Position = UDim2.new(-0.0236098338, 0, 0, 0)
	container_2.Size = UDim2.new(0, 102, 0, -2)
	container_2.ZIndex = 6

	name.Name = self.name
	name.Parent = container_2
	name.BackgroundColor3 = Color3.fromRGB(255, 255, 255)
	name.BackgroundTransparency = 1.000
	name.BorderColor3 = Color3.fromRGB(0, 0, 0)
	name.BorderSizePixel = 0
	name.Position = UDim2.new(0.024743339, 0, -8.91508198, 0)
	name.Size = UDim2.new(0, 190, 0, 17)
	name.Font = Enum.Font.GothamMedium
	name.Text = self.display_name
	name.TextColor3 = Color3.fromRGB(255, 255, 255)
	name.TextSize = 16.000
	name.TextTransparency = 0.460
	name.TextWrapped = true
	name.TextXAlignment = Enum.TextXAlignment.Left
	
    local connection
	connection = RunService.RenderStepped:Connect(function()
		if not nurysium_ui.user_gui:FindFirstChild('background') then
			connection:Disconnect()

            return
		end

		example.Visible = self.category == ui.currect_category
	end)
end

function nurysium_ui:create_toggle()
	ui.toggles[self.flag_name] = {}
	ui.toggles[self.flag_name].value = self.value
	
	local is_enabled = false :: boolean
	
	local toggle_hitbox = Instance.new("TextButton")
	local toggle = Instance.new("Frame")
	local UICorner_2 = Instance.new("UICorner")
	local example_2 = Instance.new("ImageLabel")
	local UIScale = Instance.new("UIScale")
	
	toggle_hitbox.Name = self.name
	toggle_hitbox.Parent = nurysium_ui.user_gui.background:FindFirstChild(self.side):FindFirstChild(self.section)
	toggle_hitbox.BackgroundColor3 = Color3.fromRGB(255, 255, 255)
	toggle_hitbox.BackgroundTransparency = 1.000
	toggle_hitbox.BorderColor3 = Color3.fromRGB(0, 0, 0)
	toggle_hitbox.BorderSizePixel = 0
	toggle_hitbox.Position = UDim2.new(-1.26653219e-07, 0, 0.614457846, 0)
	toggle_hitbox.Size = UDim2.new(0, 187, 0, 32)
	toggle_hitbox.Font = Enum.Font.GothamMedium
	toggle_hitbox.Text = self.display_name
	toggle_hitbox.TextColor3 = Color3.fromRGB(255, 255, 255)
	toggle_hitbox.TextSize = 18.000
	toggle_hitbox.TextWrapped = true
	toggle_hitbox.TextXAlignment = Enum.TextXAlignment.Left
	toggle_hitbox.LayoutOrder = self.layout_order

	toggle.Name = "toggle"
	toggle.Parent = toggle_hitbox
	toggle.AnchorPoint = Vector2.new(0.5, 0.5)
	toggle.BackgroundColor3 = Color3.fromRGB(27, 27, 27)
	toggle.BackgroundTransparency = 0.400
	toggle.BorderColor3 = Color3.fromRGB(0, 0, 0)
	toggle.BorderSizePixel = 0
	toggle.Position = UDim2.new(0.943697512, 0, 0.482483804, 0)
	toggle.Size = UDim2.new(0, 20, 0, 20)
	toggle.ZIndex = 15

	UICorner_2.CornerRadius = UDim.new(0.200000003, 0)
	UICorner_2.Parent = toggle

	example_2.Name = "example"
	example_2.Parent = toggle
	example_2.AnchorPoint = Vector2.new(0.5, 0.5)
	example_2.BackgroundColor3 = Color3.fromRGB(255, 255, 255)
	example_2.BackgroundTransparency = 1.000
	example_2.BorderColor3 = Color3.fromRGB(0, 0, 0)
	example_2.BorderSizePixel = 0
	example_2.Position = UDim2.new(0.519999981, 0, 0.479999989, 0)
	example_2.Size = UDim2.new(0, 10, 0, 10)
	example_2.ZIndex = 5
	example_2.Image = "rbxassetid://18497520940"
	example_2.ImageTransparency = 0.450

	UIScale.Parent = example_2
	UIScale.Scale = 0.800
	
	if self.value == true then
		TweenService:Create(UIScale, TweenInfo.new(0.3, Enum.EasingStyle.Exponential), {
			Scale = 0
		}):Play()

		TweenService:Create(example_2, TweenInfo.new(0.3, Enum.EasingStyle.Exponential), {
			ImageTransparency = 1,
			Rotation = 0
		}):Play()

		task.delay(0.3, function()
			example_2.Image = "rbxassetid://18497538632"

			TweenService:Create(UIScale, TweenInfo.new(0.45, Enum.EasingStyle.Exponential), {
				Scale = 1.25
			}):Play()

			TweenService:Create(example_2, TweenInfo.new(0.45, Enum.EasingStyle.Exponential), {
				ImageTransparency = 0
			}):Play()

			TweenService:Create(toggle, TweenInfo.new(0.65, Enum.EasingStyle.Exponential), {
				BackgroundColor3 = Color3.fromRGB(55, 97, 189)
			}):Play()
		end)
	end
	
	toggle_hitbox.MouseButton1Up:Connect(function()
		is_enabled = not is_enabled
		ui.toggles[self.flag_name].value = is_enabled

		if is_enabled then
			TweenService:Create(UIScale, TweenInfo.new(0.3, Enum.EasingStyle.Exponential), {
				Scale = 0
			}):Play()

			TweenService:Create(example_2, TweenInfo.new(0.3, Enum.EasingStyle.Exponential), {
				ImageTransparency = 1,
				Rotation = 0
			}):Play()

			task.delay(0.3, function()
				example_2.Image = "rbxassetid://18497538632"

				TweenService:Create(UIScale, TweenInfo.new(0.45, Enum.EasingStyle.Exponential), {
					Scale = 1.25
				}):Play()

				TweenService:Create(example_2, TweenInfo.new(0.45, Enum.EasingStyle.Exponential), {
					ImageTransparency = 0
				}):Play()

				TweenService:Create(toggle, TweenInfo.new(0.65, Enum.EasingStyle.Exponential), {
					BackgroundColor3 = Color3.fromRGB(55, 97, 189)
				}):Play()
			end)
		else
			TweenService:Create(UIScale, TweenInfo.new(0.3, Enum.EasingStyle.Exponential), {
				Scale = 0,
			}):Play()

			TweenService:Create(example_2, TweenInfo.new(0.3, Enum.EasingStyle.Exponential), {
				ImageTransparency = 1,
				Rotation = 180
			}):Play()

			task.delay(0.3, function()
				example_2.Image = 'rbxassetid://18497520940'

				TweenService:Create(UIScale, TweenInfo.new(0.45, Enum.EasingStyle.Exponential), {
					Scale = 0.8
				}):Play()

				TweenService:Create(example_2, TweenInfo.new(0.45, Enum.EasingStyle.Exponential), {
					ImageTransparency = 0.45
				}):Play()

				TweenService:Create(toggle, TweenInfo.new(0.65, Enum.EasingStyle.Exponential), {
					BackgroundColor3 = Color3.fromRGB(27, 27, 27)
				}):Play()
			end)
		end
	end)
	
	toggle_hitbox.TouchTap:Connect(function()
		is_enabled = not is_enabled
		ui.toggles[self.flag_name].value = is_enabled

		if is_enabled then
			TweenService:Create(UIScale, TweenInfo.new(0.3, Enum.EasingStyle.Exponential), {
				Scale = 0
			}):Play()

			TweenService:Create(example_2, TweenInfo.new(0.3, Enum.EasingStyle.Exponential), {
				ImageTransparency = 1,
				Rotation = 0
			}):Play()

			task.delay(0.3, function()
				example_2.Image = "rbxassetid://18497538632"

				TweenService:Create(UIScale, TweenInfo.new(0.45, Enum.EasingStyle.Exponential), {
					Scale = 1.25
				}):Play()

				TweenService:Create(example_2, TweenInfo.new(0.45, Enum.EasingStyle.Exponential), {
					ImageTransparency = 0
				}):Play()

				TweenService:Create(toggle, TweenInfo.new(0.65, Enum.EasingStyle.Exponential), {
					BackgroundColor3 = Color3.fromRGB(55, 97, 189)
				}):Play()
			end)
		else
			TweenService:Create(UIScale, TweenInfo.new(0.3, Enum.EasingStyle.Exponential), {
				Scale = 0,
			}):Play()

			TweenService:Create(example_2, TweenInfo.new(0.3, Enum.EasingStyle.Exponential), {
				ImageTransparency = 1,
				Rotation = 180
			}):Play()

			task.delay(0.3, function()
				example_2.Image = 'rbxassetid://18497520940'

				TweenService:Create(UIScale, TweenInfo.new(0.45, Enum.EasingStyle.Exponential), {
					Scale = 0.8
				}):Play()

				TweenService:Create(example_2, TweenInfo.new(0.45, Enum.EasingStyle.Exponential), {
					ImageTransparency = 0.45
				}):Play()

				TweenService:Create(toggle, TweenInfo.new(0.65, Enum.EasingStyle.Exponential), {
					BackgroundColor3 = Color3.fromRGB(27, 27, 27)
				}):Play()
			end)
		end
	end)
end

function nurysium_ui:create_dropdown()
	local dropdown = Instance.new("TextButton")
	local example = Instance.new("ImageLabel")
	local UIScale = Instance.new("UIScale")

	dropdown.Name = self.name
	dropdown.Parent = nurysium_ui.user_gui.background:FindFirstChild(self.side):FindFirstChild(self.section)
	dropdown.BackgroundColor3 = Color3.fromRGB(255, 255, 255)
	dropdown.BackgroundTransparency = 1.000
	dropdown.BorderColor3 = Color3.fromRGB(0, 0, 0)
	dropdown.BorderSizePixel = 0
	dropdown.Position = UDim2.new(-1.26653219e-07, 0, 0.614457846, 0)
	dropdown.Size = UDim2.new(0, 187, 0, 32)
	dropdown.Font = Enum.Font.GothamMedium
	dropdown.Text = self.display_name
	dropdown.TextColor3 = Color3.fromRGB(255, 255, 255)
	dropdown.TextSize = 18.000
	dropdown.TextWrapped = true
	dropdown.TextXAlignment = Enum.TextXAlignment.Left
	dropdown.LayoutOrder = self.layout_order

	example.Name = "example"
	example.Parent = dropdown
	example.AnchorPoint = Vector2.new(0.5, 0.5)
	example.BackgroundColor3 = Color3.fromRGB(255, 255, 255)
	example.BackgroundTransparency = 1.000
	example.BorderColor3 = Color3.fromRGB(0, 0, 0)
	example.BorderSizePixel = 0
	example.Position = UDim2.new(0.941182196, 0, 0.480000645, 0)
	example.Rotation = 90.000
	example.Size = UDim2.new(0, 10, 0, 10)
	example.ZIndex = 5
	example.Image = "rbxassetid://18928192021"
	example.ImageTransparency = 0.450

	UIScale.Parent = example
	UIScale.Scale = 1.190

	RunService.RenderStepped:Wait()

	local dropdown_frame = Instance.new("ScrollingFrame")
	local UIScale = Instance.new("UIScale")
	local UIListLayout = Instance.new("UIListLayout")
	local UIPadding = Instance.new("UIPadding")

	dropdown_frame.Name = self.name
	dropdown_frame.Parent = nurysium_ui.user_gui.background:FindFirstChild(self.side):FindFirstChild(self.section).container
	dropdown_frame.BackgroundColor3 = Color3.fromRGB(22, 22, 22)
	dropdown_frame.BorderColor3 = Color3.fromRGB(0, 0, 0)
	dropdown_frame.BorderSizePixel = 0
	dropdown_frame.AnchorPoint = Vector2.new(0.5, 0.5)
	dropdown_frame.BackgroundTransparency = 1
	dropdown_frame.Position = UDim2.new(1.20039442e-06, 0, 0.378109604, 0)
	dropdown_frame.Size = UDim2.new(0, 185, 0, 125)
	dropdown_frame.Active = true
	dropdown_frame.ZIndex = 6
	dropdown_frame.ScrollBarThickness = 0
	dropdown_frame.LayoutOrder = self.layout_order

	UIScale.Scale = 0
	UIScale.Parent = dropdown_frame

	UIListLayout.Parent = dropdown_frame
	UIListLayout.HorizontalAlignment = Enum.HorizontalAlignment.Center
	UIListLayout.SortOrder = Enum.SortOrder.LayoutOrder
	UIListLayout.Padding = UDim.new(0, 4)

	UIPadding.Parent = dropdown_frame
	UIPadding.PaddingTop = UDim.new(0.015, 0)
	
	local on_animation = false :: boolean
	local is_opened = false :: boolean
	
	dropdown.MouseButton1Up:Connect(function()		
		if on_animation then
			return
		end
		
		on_animation = true
		is_opened = not is_opened
		
		if is_opened then
			TweenService:Create(UIScale, TweenInfo.new(1, Enum.EasingStyle.Exponential), {
				Scale = 1
			}):Play()
			
			TweenService:Create(dropdown_frame, TweenInfo.new(0.65, Enum.EasingStyle.Exponential), {
				Transparency = 0,
				ScrollBarThickness = 2
			}):Play()

			dropdown_frame.Parent = nurysium_ui.user_gui.background:FindFirstChild(self.side):FindFirstChild(self.section)
			
			task.delay(1, function()
				on_animation = false
			end)
			
			TweenService:Create(dropdown, TweenInfo.new(1, Enum.EasingStyle.Back), {
				TextColor3 = Color3.fromRGB(55, 97, 189)
			}):Play()
			
			TweenService:Create(example, TweenInfo.new(1, Enum.EasingStyle.Back), {
				Rotation = -90
			}):Play()
		else
			TweenService:Create(UIScale, TweenInfo.new(0.5, Enum.EasingStyle.Exponential), {
				Scale = 0
			}):Play()
			
			TweenService:Create(dropdown_frame, TweenInfo.new(0.25, Enum.EasingStyle.Exponential), {
				Transparency = 1,
				ScrollBarThickness = 0
			}):Play()

			task.delay(0.45, function()
				dropdown_frame.Parent = nurysium_ui.user_gui.background:FindFirstChild(self.side):FindFirstChild(self.section).container
			end)
			
			task.delay(1, function()
				on_animation = false
			end)

			TweenService:Create(dropdown, TweenInfo.new(1, Enum.EasingStyle.Back), {
				TextColor3 = Color3.fromRGB(255, 255, 255)
			}):Play()
			
			TweenService:Create(example, TweenInfo.new(1, Enum.EasingStyle.Back), {
				Rotation = 90
			}):Play()
		end
	end)

	dropdown.TouchTap:Connect(function()		
		if on_animation then
			return
		end

		on_animation = true
		is_opened = not is_opened

		if is_opened then
			TweenService:Create(UIScale, TweenInfo.new(1, Enum.EasingStyle.Exponential), {
				Scale = 1
			}):Play()

			TweenService:Create(dropdown_frame, TweenInfo.new(0.65, Enum.EasingStyle.Exponential), {
				Transparency = 0,
				ScrollBarThickness = 2
			}):Play()

			dropdown_frame.Parent = nurysium_ui.user_gui.background:FindFirstChild(self.side):FindFirstChild(self.section)

			task.delay(1, function()
				on_animation = false
			end)

			TweenService:Create(dropdown, TweenInfo.new(1, Enum.EasingStyle.Back), {
				TextColor3 = Color3.fromRGB(55, 97, 189)
			}):Play()

			TweenService:Create(example, TweenInfo.new(1, Enum.EasingStyle.Back), {
				Rotation = -90
			}):Play()
		else
			TweenService:Create(UIScale, TweenInfo.new(0.5, Enum.EasingStyle.Exponential), {
				Scale = 0
			}):Play()

			TweenService:Create(dropdown_frame, TweenInfo.new(0.25, Enum.EasingStyle.Exponential), {
				Transparency = 1,
				ScrollBarThickness = 0
			}):Play()

			task.delay(0.45, function()
				dropdown_frame.Parent = nurysium_ui.user_gui.background:FindFirstChild(self.side):FindFirstChild(self.section).container
			end)

			task.delay(1, function()
				on_animation = false
			end)

			TweenService:Create(dropdown, TweenInfo.new(1, Enum.EasingStyle.Back), {
				TextColor3 = Color3.fromRGB(255, 255, 255)
			}):Play()

			TweenService:Create(example, TweenInfo.new(1, Enum.EasingStyle.Back), {
				Rotation = 90
			}):Play()
		end
	end)
	
	if not dropdown_frame then
		warn('[UI] failed to add dropdown mode, dropdown is null.')

		return
	end
	
	local current_table = {}
	table.insert(ui.dropdowns, current_table)
	
	for fabric_id = 1, #self.mods do
		local current_mode = self.mods[fabric_id]
		
		local current_mode = Instance.new("TextButton")
		
		current_mode.Name = self.name
		current_mode.Parent = dropdown_frame
		current_mode.BackgroundColor3 = Color3.fromRGB(255, 255, 255)
		current_mode.BackgroundTransparency = 1.000
		current_mode.BorderColor3 = Color3.fromRGB(0, 0, 0)
		current_mode.BorderSizePixel = 0
		current_mode.Position = UDim2.new(-1.26653219e-07, 0, 0.614457846, 0)
		current_mode.Size = UDim2.new(0, 187, 0, 15)
		current_mode.Font = Enum.Font.GothamMedium
		current_mode.Text = self.mods[fabric_id]
		current_mode.TextColor3 = Color3.fromRGB(255, 244, 243)
		current_mode.TextScaled = true
		current_mode.TextSize = 14.000
		current_mode.TextTransparency = 0.15
		current_mode.TextWrapped = true
		current_mode.ZIndex = 70

        print(selected_mod)
        if self.selected_mod == current_mode.Text then
            table.insert(current_table, current_mode.Text)
        end
		
		current_mode.MouseButton1Up:Connect(function()
			table.clear(current_table)
			table.insert(current_table, current_mode.Text)
		end)

		current_mode.TouchTap:Connect(function()
			table.clear(current_table)
			table.insert(current_table, current_mode.Text)
		end)

        local connection
		connection = RunService.RenderStepped:Connect(function()
			if not nurysium_ui.user_gui:FindFirstChild('background') then
				connection:Disconnect()

                return
			end

			if not table.find(current_table, current_mode.Text) then
				TweenService:Create(current_mode, TweenInfo.new(1, Enum.EasingStyle.Back), {
					TextColor3 = Color3.fromRGB(255, 244, 243)
				}):Play()
				
				return
			end
			
			TweenService:Create(current_mode, TweenInfo.new(1, Enum.EasingStyle.Back), {
				TextColor3 = Color3.fromRGB(55, 97, 189)
			}):Play()
		end)
	end 
end

function ui:get_slider_value()
	if not self then
		warn('[UI] get slider value error, forgot about table?')
		
		return
	end

	return (mouse.X - self.slider_frame.AbsolutePosition.X) / self.slider_frame.AbsoluteSize.X * 100
end

function nurysium_ui:create_slider()
	local slider_text = Instance.new("Frame")
	local UICorner = Instance.new("UICorner")
	local container = Instance.new("Frame")
	local container_2 = Instance.new("Frame")
	local name = Instance.new("TextLabel")

	slider_text.Name = self.name .. "slider_text"
	slider_text.Parent = nurysium_ui.user_gui.background:FindFirstChild(self.side):FindFirstChild(self.section)
	slider_text.BackgroundColor3 = Color3.fromRGB(22, 22, 22)
	slider_text.BackgroundTransparency = 1.000
	slider_text.BorderColor3 = Color3.fromRGB(0, 0, 0)
	slider_text.BorderSizePixel = 0
	slider_text.Position = UDim2.new(6.45699515e-07, 0, 0.940347612, 0)
	slider_text.Size = UDim2.new(0, 185, 0, 15)
	slider_text.LayoutOrder = self.layout_order

	UICorner.CornerRadius = UDim.new(0, 10)
	UICorner.Parent = slider_text

	container.Name = "container"
	container.Parent = slider_text
	container.AnchorPoint = Vector2.new(0.5, 0.5)
	container.BackgroundColor3 = Color3.fromRGB(17, 2, 11)
	container.BackgroundTransparency = 1.000
	container.BorderColor3 = Color3.fromRGB(0, 0, 0)
	container.BorderSizePixel = 0
	container.Position = UDim2.new(0.477588803, 0, 0.992289364, 0)
	container.Size = UDim2.new(1, 0, -0.0154226189, 0)
	container.ZIndex = 6

	container_2.Name = "container"
	container_2.Parent = container
	container_2.BackgroundColor3 = Color3.fromRGB(17, 2, 11)
	container_2.BackgroundTransparency = 1.000
	container_2.BorderColor3 = Color3.fromRGB(0, 0, 0)
	container_2.BorderSizePixel = 0
	container_2.Position = UDim2.new(0.0194419082, 0, 20.1267948, 0)
	container_2.Size = UDim2.new(0, 129, 0, -2)
	container_2.ZIndex = 6

	name.Name = self.name
	name.Parent = container_2
	name.BackgroundColor3 = Color3.fromRGB(255, 255, 255)
	name.BackgroundTransparency = 1.000
	name.BorderColor3 = Color3.fromRGB(0, 0, 0)
	name.BorderSizePixel = 0
	name.Position = UDim2.new(0.024743339, 0, -8.91508198, 0)
	name.Size = UDim2.new(0, 190, 0, 17)
	name.Font = Enum.Font.GothamMedium
	name.Text = self.display_name
	name.TextColor3 = Color3.fromRGB(255, 255, 255)
	name.TextSize = 12.000
	name.TextTransparency = 0.460
	name.TextWrapped = true
	name.TextXAlignment = Enum.TextXAlignment.Left

	local slider_active = false :: boolean
	
	ui.sliders[self.name] = {}
	ui.sliders[self.name].value = self.value

	task.defer(function()
		local slider = Instance.new("Frame")
		local UICorner = Instance.new("UICorner")
		local example = Instance.new("TextButton")
		local filler = Instance.new("Frame")
		local UICorner_2 = Instance.new("UICorner")

        local connection
		connection = RunService.RenderStepped:Connect(function()
			if not nurysium_ui.user_gui:FindFirstChild('background') then
				connection:Disconnect()

                return
			end

			if not ui.open or not slider_active then
				return
			end

			local value = math.clamp(ui.get_slider_value({
				slider_frame = slider 
			}), self.min_value, self.max_value)
			
			local round_value = math.round(value)

			ui.sliders[self.name].value = round_value
			example.Text = round_value

            local size_value = value / self.max_value

			TweenService:Create(filler, TweenInfo.new(1, Enum.EasingStyle.Exponential), {
				Size = UDim2.new(size_value, 0, 1, 0),
				BackgroundTransparency = 0.5
			}):Play()
		end)

		slider.Name = "slider"
		slider.Parent = nurysium_ui.user_gui.background:FindFirstChild(self.side):FindFirstChild(self.section)
		slider.BackgroundColor3 = Color3.fromRGB(22, 22, 22)
		slider.BorderColor3 = Color3.fromRGB(0, 0, 0)
		slider.BorderSizePixel = 0
		slider.Position = UDim2.new(6.45699515e-07, 0, 0.940347612, 0)
		slider.Size = UDim2.new(0, 185, 0, 10)
		slider.LayoutOrder = self.layout_order

		UICorner.CornerRadius = UDim.new(0, 10)
		UICorner.Parent = slider

		example.Name = "example"
		example.Parent = slider
		example.BackgroundColor3 = Color3.fromRGB(255, 255, 255)
		example.BackgroundTransparency = 1.000
		example.BorderColor3 = Color3.fromRGB(0, 0, 0)
		example.BorderSizePixel = 0
		example.Position = UDim2.new(-0.00478354469, 0, -0.00501244469, 0)
		example.Size = UDim2.new(1, 0, 1, 0)
		example.ZIndex = 8
		example.Font = Enum.Font.GothamMedium
		example.Text = self.value
		example.TextColor3 = Color3.fromRGB(255, 247, 244)
		example.TextScaled = false
		example.TextSize = 9
		example.TextTransparency = 0.500
		example.TextWrapped = true
		
		example.MouseButton1Down:Connect(function()
			slider_active = true
		end)

		example.TouchTap:Connect(function()
			slider_active = true
		end)

		UserInputService.InputEnded:Connect(function(input: InputObject, is_event: boolean)
			if input.UserInputType == Enum.UserInputType.MouseButton1 or input.UserInputType == Enum.UserInputType.Touch then
				slider_active = false

				TweenService:Create(filler, TweenInfo.new(0.45, Enum.EasingStyle.Exponential), {
					BackgroundTransparency = 0
				}):Play()
			end
		end)
		
		filler.Name = "filler"
		filler.Parent = slider
		filler.BackgroundColor3 = Color3.fromRGB(55, 97, 189)
		filler.BorderColor3 = Color3.fromRGB(0, 0, 0)
		filler.BorderSizePixel = 0
		filler.Position = UDim2.new(0, 0, -1.08026825e-05, 0)
		filler.Size = UDim2.new(self.value / self.max_value, 0, 1, 0)

		UICorner_2.CornerRadius = UDim.new(0, 10)
		UICorner_2.Parent = filler
	end)
end

function nurysium_ui.ui_is_valid()
	return nurysium_ui.user_gui:FindFirstChild('background')
end

function nurysium_ui:get_toggle_value()
	if not ui.toggles[self.flag] then
		return
	end

	local boolean = ui.toggles[self.flag]['value'] :: boolean

	return boolean
end

function nurysium_ui:get_slider_value()
	if not ui.sliders[self.flag] then
		return
	end

	return ui.sliders[self.flag]['value']
end

function nurysium_ui:get_dropdown_value()
	for custom_unpack = 1, #ui.dropdowns do
		local current_dropdown = ui.dropdowns[custom_unpack]
		
		return table.find(current_dropdown, self.flag) ~= nil
	end
end

return nurysium_ui
