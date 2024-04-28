## Getting Started
Our game involves an alien (Space Dude) and a series of platforms. The goal is to jump from the start magenta platform to the final green platform
Points are awarded with each successful initial jump onto a platform. Bonus points are available if you make it to the end. Points are deducted for platform collisions with Space Dude.

## Control
Up arrow key controls the jumping
Left and Right arrow keys control Space Dude on platforms and in the air 

## Structure
### Ball Class
This was the original player class but it is currently unused.
Implements a key listener and extends the Rectangle class to help with collision detection
### Player Class
The Player Class implements a key listener to control Space Dude. 
The Player Class is a child of the Rectangle parent class whose methods are used for collsion detection with platforms.
Methods
fetchImage and fetchAudio are used to load the sprite image and the jump sound.

draw is used to draw the sprite image accurately on platforms by accounting for the gap between the platform and the image.

jump sets inAir to true and initializes airSpeed so Space Dude can move from one platform to another. The jump sound is triggered in this method.

getBounds is used to update the position of Space Dude's hitbox for collision detection.

onPlatform returns a boolean value depending on whether Space Dude is on a platform by comparing the midpoint of SpaceDude's hitbox with the start X and end X positions of the platform

getClosestPlatform returns the closest platform to Space Dude by checking whether the midpoint of Space Dude's hitbox is within half the platform spacing of any platform in an arraylist of platforms.

canMoveHere returns a boolean value after evaluating whether the nextPlace of Space Dude's hitbox is valid. A valid nextPlace is bounded by the game window apart from below AND A valid nextPlace does not intersect with a platform.

updatePosition is super chunky:
- First, we get the closest platform using getClosestPlatform.
- Next, we check if Space Dude is on the closest platform.
- If Space Dude is not on the closest platform, then Space Dude is in space (air)
- If Space Dude is in air, Space Dude is not allowed to jump.
- If the space above Space Dude is clear (no platforms), Space Dude can move into that space and Space Dude's position is updated else:
-- If Space Dude in on the edge of a platform (technically not on the platform), the program throws Space Dude off the platform.
-- If Space Dude is still moving upwards(airSpeed < 0), Space Dude has banged into a platform therefore, the program updates Space Dude's postion to be right under the platform Space Dude has banged into.
-- If Space Dude is falling downwards(airSpeed > 0) AND Space Dude is on the closest Platform, the program updates Space Dude's position to be right above the platform Space Dude has landed on. The program calls landed to set inAir to false and airSpeed to 0.
-- Else  Space Dude is stil falling.
- If Space Dude is attempting to move left or right and the position is valid, the program updates Space Dude's position.
- The updatePosition method makes it difficult to move left or right while inAir by failing to update the Space Dude's x position at the same time as Space Dude's y position.
### Platform Class
Extends Rectangle class for collsion detection
Contains instance variables to initialize the platform on the canvas with different colors, speeds, and static state.
Each platform also has a visit status and lastPlaform status used to award points in the GamePanel class.
### GamePanel
Implements runnable for the use of a separate thread to make the game run smoothly
Everything interacts in here largely
Methods
initializePlatforms is another chunky one:
- Every game has 7 platforms; the magenta one is static while the rest move from left to right within the game window.
- The program calculates platformSpacing between each platform and the screen size.
- The start (magenta) and last (green) platforms are intialized manually with the last platform always having a speed of 2 and direction of right. The start platform is initialized as visited because Space Dude spawns here.
- An array list of Platform is used to store all the platforms. It is named accordingly.
- The program generates random speeds (3 to 7) and ensures there's a difference of at least 2 between contiguous platforms.
- The speeds are stored in an arraylist(speedsArr) which is used to assign speeds to instances of the Platform class.
- These instances are given the same color(blue), the same x position and differnt y positions calculated based on the platformSpacing and the number of platforms above. 
- The platforms are sorted from top to bottom as they appear on the screen. This was initially done to make collsion detection faster and worked for the most part with the ball class(see previous versions on github)
- The game could be made faster by shortening the time spent comparing Space Dude's position and other platforms but since it's not broken... save it for another day.

run must be used since the GamePanel implements a runnable interface.
- The program uses an infinite loop to handle rendering(painting) and updating(calculations) separately. 
- First, time is returned in nanoseconds for higher accuracy in prevUpdate and prevFrame as initial values. 
- frames, lastCheck and updates variables are used to output the FPS and UPS based on the FPS_SET and UPS_SET.
- While the game is running, the program loops the background music and calculates the percentage of the update(deltaU) interval and repainting(deltaF) interval.
- If these intervals are greater than or equal to 100%, update and repaint methods are called respectively. The prevUpdate and prevFrame time variables are updated. This method ensures that lost time is accounted for by repainting or updating sooner since we subtract 100% any remainder is carried forward to the next iteration.
- The program outputs the UPS and FPS every second.

update loops through every platform in the platforms arraylist and updates its position if it is non-static. 
- If the Space Dude collides with any of the platform's sides, Space Dude's speed is set to match that of the platform, a sound effect is played and the score is updated.
- Space Dude's updatePosition method ensures that gravity is always in effect.
- If Space Dude makes it to an unvisited platform, the program updates the score, plays a sound effect, and updates the that platform's visited status.
- If Space Dude makes it to the final platform, the program updates the score, a sound effect is played and the win condition is updated.
- If the win condition is true, all moving platforms are stopped, a sound effect is played, and the game stops. A congratulations message is displayed with the score. 
- If Space Dude falls below the start platform, a sound effect is played, a message is displayed and the game stops. The messages are the conditions are check inside the paintComponent method.

paintComponent renders all the graphics in the game.

### WinPanel
A simple JComponent to output a message that signals the successful completion of the game as well as the score.

### LosePanel
A simple JComponent to output a message that signals failure to complete the game objective as well as the score.

## Proud moments
* Making comments: you'll find that our code is well documented
* Figuring out collision detection 
* Figuring out gravity
* Using different Youtube videos (Kaarin Gaming(Hitbox and gravity) and BroCode(Collsion detection with rectangles) channels were really helpful)
* We made it pretty 😍

## Bugs
* Collision detection
* Random speed generation
