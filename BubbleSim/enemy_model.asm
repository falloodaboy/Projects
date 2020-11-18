.include "convenience.asm"
.include "game_settings.asm"
.data
	#typedef enemy_struct{
	#	int x_pos;
	#	int y_pos;
	#	int dir;
	#}enemy_struct;
	enemy_struct_1: .word 14, 5, 0
	enemy_struct_2: .word 27, 35, 0
	enemy_struct_3: .word 56, 45, 0
	enemy_struct_4: .word 45, 45, 0
	enemy_struct_5: .word 28, 45, 0
	enemy_sprite: .byte COLOR_NONE, COLOR_NONE, COLOR_WHITE, COLOR_NONE, COLOR_NONE
			    COLOR_WHITE, COLOR_WHITE, COLOR_ORANGE, COLOR_WHITE, COLOR_WHITE
			    COLOR_NONE, COLOR_ORANGE, COLOR_ORANGE, COLOR_ORANGE, COLOR_NONE
			    COLOR_NONE, COLOR_WHITE, COLOR_NONE, COLOR_WHITE, COLOR_NONE
			    COLOR_NONE, COLOR_WHITE, COLOR_NONE, COLOR_WHITE, COLOR_NONE

   enemy_sprite_blank: .byte	COLOR_NONE, COLOR_NONE, COLOR_NONE, COLOR_NONE, COLOR_NONE
				COLOR_NONE, COLOR_NONE, COLOR_NONE, COLOR_NONE, COLOR_NONE
				COLOR_NONE, COLOR_NONE, COLOR_NONE, COLOR_NONE, COLOR_NONE
				COLOR_NONE, COLOR_NONE, COLOR_NONE, COLOR_NONE, COLOR_NONE
				COLOR_NONE, COLOR_NONE, COLOR_NONE, COLOR_NONE, COLOR_NONE
.globl enemy_sprite
.globl enemy_sprite_blank
.globl enemy_struct_1
.globl enemy_struct_2
.globl enemy_struct_3
.globl enemy_struct_4
.globl enemy_struct_5
