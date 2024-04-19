## Getting Started
Our game involves a ball and a series of platforms. The goal is to jump from the start magenta platform to the final green platform
Points are awarded with each successful initial jump onto a platform. Bonus points are available if you make it to the end.

## Control
Up arrow key controls the jumping
Left and Right arrow key controls are yet to be perfected as their input is washed out by the platform speed
When the ball lands on a platform it's x-speed is set to match that of the platforms

## Structure
* Ball Class
Implements a key listener and extends the Rectangle class to help with collision detection
* Platform Class
Extends Rectangle class for collsion detection
Contains instance variables to initialize the platform on the canvas with different colors, speeds, and static state
* GamePanel
Implements runnable for the use of a separate thread to make the game run smoothly
Everything interacts in here largely

## Proud moments
* Making comments: you'll find that our code is well documented
* Figuring out collision detection using a hitbox(the magenta rectangle around the ball)
* Figuring out gravity (we cried)
* Using try-catch to avoid and IndexOutOfBoundsException because we were too smart 😁 to do the Math
* Using different Youtube videos (Kaarin Gaming(Hitbox and gravity) and BroCode(Collsion detection with rectangles) channels were really helpful)

## Work to be done
* It's ugly so we'll be working to make it pretty 😍
