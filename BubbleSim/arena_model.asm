#stores all of the models for the game
.include "game_settings.asm"
.globl arena
.globl wallblock
.globl lives
.globl game_over
.globl enemies_left
.data
	#model for the wallblocks to draw on the display
	wallblock: .byte COLOR_GREEN, COLOR_GREEN, COLOR_BLACK, COLOR_GREEN, COLOR_GREEN
			 COLOR_GREEN,COLOR_GREEN, COLOR_BLACK, COLOR_GREEN, COLOR_GREEN
			 COLOR_BLACK, COLOR_BLACK, COLOR_RED, COLOR_BLACK, COLOR_BLACK
			 COLOR_GREEN, COLOR_GREEN, COLOR_BLACK, COLOR_GREEN, COLOR_GREEN
			 COLOR_GREEN, COLOR_GREEN, COLOR_BLACK, COLOR_GREEN, COLOR_GREEN
			 #COLOR_WHITE, COLOR_WHITE, COLOR_WHITE, COLOR_WHITE, COLOR_WHITE
			# COLOR_WHITE, COLOR_WHITE, COLOR_WHITE, COLOR_WHITE, COLOR_WHITE
			# COLOR_BLUE, COLOR_BLUE, COLOR_BLUE, COLOR_BLUE, COLOR_BLUE
			# COLOR_BLUE, COLOR_BLUE, COLOR_BLUE, COLOR_BLUE, COLOR_BLUE
			# COLOR_RED, COLOR_RED, COLOR_RED, COLOR_RED, COLOR_RED
			 
			 
	#The arena variable is the map for the game arena and will be crucial to determine where the walls are. Consequently, when
	#drawing the arena, whenever the iterator is greater than 56, then reset the x position to 2. This ensure no blocks are placed where the player can't get through
	arena:	.byte 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
	  	      0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
	  	      0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0
	  	      0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
	  	      0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
	  	      0, 1, 1, 0, 1, 0, 0, 1, 0, 0, 0, 0
	  	      0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0
	  	      0, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0
	  	      0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0
	  	      0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
	  	      1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1
	  	     # 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1
	  	  #    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
	#standard of 3 lives for the player
	lives: .byte 3
	enemies_left: .byte 5
	game_over: .byte 0 #set to 1 if 
