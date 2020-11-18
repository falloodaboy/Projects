.include "game_settings.asm"
.data
#Can see if the LEDs next to the player block are on which I can check to see if there is a wall or enemy here
#OR: divide x by 5 to see which block you are in
	#player_struct{
	  #x_pos: .word 0	4
	  #y_pos: .word 0	4
	  #direction: .word 0	4 #should be 0 for left, 1 for up, 2 for right
	  #invincible: .word 0	4 #should be 0 for false, 1 for true
	#}
	player_struct: .word 2, 45, 0, 0
	player_sprite: .byte	COLOR_NONE, COLOR_NONE, COLOR_WHITE, COLOR_NONE, COLOR_NONE
				COLOR_WHITE, COLOR_WHITE, COLOR_GREEN, COLOR_WHITE, COLOR_WHITE
				COLOR_NONE, COLOR_GREEN, COLOR_GREEN, COLOR_GREEN, COLOR_NONE
				COLOR_NONE, COLOR_WHITE, COLOR_WHITE, COLOR_WHITE, COLOR_NONE
				COLOR_NONE, COLOR_WHITE, COLOR_NONE, COLOR_WHITE, COLOR_NONE
	
  player_sprite_blank: .byte	COLOR_NONE, COLOR_NONE, COLOR_NONE, COLOR_NONE, COLOR_NONE
				COLOR_NONE, COLOR_NONE, COLOR_NONE, COLOR_NONE, COLOR_NONE
				COLOR_NONE, COLOR_NONE, COLOR_NONE, COLOR_NONE, COLOR_NONE
				COLOR_NONE, COLOR_NONE, COLOR_NONE, COLOR_NONE, COLOR_NONE
				COLOR_NONE, COLOR_NONE, COLOR_NONE, COLOR_NONE, COLOR_NONE

.globl player_struct
.globl player_sprite
.globl player_sprite_blank
