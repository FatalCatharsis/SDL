/*
  Copyright (C) 1997-2023 Sam Lantinga <slouken@libsdl.org>

  This software is provided 'as-is', without any express or implied
  warranty.  In no event will the authors be held liable for any damages
  arising from the use of this software.

  Permission is granted to anyone to use this software for any purpose,
  including commercial applications, and to alter it and redistribute it
  freely.
*/

/* Gamepad image */

typedef struct GamepadImage GamepadImage;

extern GamepadImage *CreateGamepadImage(SDL_Renderer *renderer);
extern void SetGamepadImagePosition(GamepadImage *ctx, int x, int y);
extern void SetGamepadImageShowingFront(GamepadImage *ctx, SDL_bool showing_front);
extern void SetGamepadImageShowingTouchpad(GamepadImage *ctx, SDL_bool showing_touchpad);
extern void GetGamepadImageArea(GamepadImage *ctx, int *x, int *y, int *width, int *height);
extern int GetGamepadImageButtonWidth(GamepadImage *ctx);
extern int GetGamepadImageButtonHeight(GamepadImage *ctx);
extern int GetGamepadImageAxisWidth(GamepadImage *ctx);
extern int GetGamepadImageAxisHeight(GamepadImage *ctx);

extern SDL_GamepadButton GetGamepadImageButtonAt(GamepadImage *ctx, float x, float y);
extern SDL_GamepadAxis GetGamepadImageAxisAt(GamepadImage *ctx, float x, float y);

extern void ClearGamepadImage(GamepadImage *ctx);
extern void SetGamepadImageButton(GamepadImage *ctx, SDL_GamepadButton button, SDL_bool active);
extern void SetGamepadImageAxis(GamepadImage *ctx, SDL_GamepadAxis axis, int direction);

extern void UpdateGamepadImageFromGamepad(GamepadImage *ctx, SDL_Gamepad *gamepad);
extern void RenderGamepadImage(GamepadImage *ctx);
extern void DestroyGamepadImage(GamepadImage *ctx);

/* Gamepad element display */

typedef struct GamepadDisplay GamepadDisplay;

extern GamepadDisplay *CreateGamepadDisplay(SDL_Renderer *renderer);
extern void SetGamepadDisplayArea(GamepadDisplay *ctx, int x, int y, int w, int h);
extern void RenderGamepadDisplay(GamepadDisplay *ctx, SDL_Gamepad *gamepad);
extern void DestroyGamepadDisplay(GamepadDisplay *ctx);

/* Joystick element display */

typedef struct JoystickDisplay JoystickDisplay;

extern JoystickDisplay *CreateJoystickDisplay(SDL_Renderer *renderer);
extern void SetJoystickDisplayArea(JoystickDisplay *ctx, int x, int y, int w, int h);
extern void RenderJoystickDisplay(JoystickDisplay *ctx, SDL_Joystick *joystick);
extern void DestroyJoystickDisplay(JoystickDisplay *ctx);

