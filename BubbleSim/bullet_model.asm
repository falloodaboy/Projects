.include "game_settings.asm"
.include "convenience.asm"

.data
	#typedef struct bullet {
	#	int x_pos;
	#	int y_pos;
	#	int dir;
	#}bullet_struct;
	bullet_struct:	.word 0, 0, 0
	bullet_sprite:	.byte	COLOR_ORANGE, COLOR_BLUE, COLOR_BLUE, COLOR_GREEN
	bullet_sprite_rev: .byte COLOR_BLUE,COLOR_BLUE, COLOR_ORANGE, COLOR_GREEN
	in_motion: .byte 0
.globl bullet_struct
.globl bullet_sprite
.globl bullet_sprite_rev
.globl in_motion
