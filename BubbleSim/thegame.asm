#mzw14
#Zohaib Wasim
#To stop the game, make sure you end the program before closing the display as otherwise it causes a problem
.data
	you_lost_string: .asciiz "YOU LOST"
	you_won_string: .asciiz "YOU WON!!"
.include "convenience.asm"
.include "game_settings.asm"
#	Defines the number of frames per second: 16ms -> 60fps
.eqv	GAME_TICK_MS		30 #18
.data
# don't get rid of these, they're used by wait_for_next_frame.
last_frame_time:  .word 0
frame_counter:    .word 0


.text
# --------------------------------------------------------------------------------------------------

.globl game
game:
	# set up anything you need to here,
	# and wait for the user to press a key to start.
	 li	a0, 2
	 li	a1, ARENA_HEIGHT
	 li	a2, ARENA_WIDTH
	 jal	 print_scoreboard
	 jal	 initialize_board
	 li	a0, 2
	 li	a1, 0
	 jal	print_arena_walls
	 jal	display_update_and_clear
	# Wait for a key input
_game_wait:
	jal	input_get_keys
	beqz	v0, _game_wait

_game_loop:
	# check for input,
	
	jal     handle_input

	# update everything,

	la	a0, player_struct
	jal	player_update
	
	la	a0, enemy_struct_1
	la	a1, arena
	jal	enemy_movement
	
	la	a0, player_struct
	la	a1, enemy_struct_1
	jal	collision_with_player
	
	la	a0, enemy_struct_1
	la	a1, bullet_struct
	la	a2, enemies_left
	jal	bullet_hit
	
	la	a0, enemy_struct_2
	la	a1, arena
	jal	enemy_movement
	
	la	a0, player_struct
	la	a1, enemy_struct_2
	jal	collision_with_player
	
	la	a0, enemy_struct_2
	la	a1, bullet_struct
	la	a2, enemies_left
	jal	bullet_hit
	
	la	a0, enemy_struct_3
	la	a1, arena
	jal	enemy_movement
	
	la	a0, player_struct
	la	a1, enemy_struct_3
	jal	collision_with_player
	
	la	a0, enemy_struct_3
	la	a1, bullet_struct
	la	a2, enemies_left
	jal	bullet_hit
	
	la	a0, enemy_struct_4
	la	a1, arena
	jal	enemy_movement
	
	la	a0, player_struct
	la	a1, enemy_struct_4
	jal	collision_with_player
	
	la	a0, enemy_struct_4
	la	a1, bullet_struct
	la	a2, enemies_left
	jal	bullet_hit
	
	la	a0, enemy_struct_5
	la	a1, arena
	jal	enemy_movement
	
	la	a0, player_struct
	la	a1,enemy_struct_5
	jal	collision_with_player
	
	la	a0, enemy_struct_5
	la	a1, bullet_struct
	la	a2, enemies_left
	jal	bullet_hit
	 #Check to see if the bullet has been fired
	 la	a0, bullet_sprite
	 la	a1, bullet_struct
	 jal	bullet_update
	 
	 
	# draw everything
	 li	a0, 2
	 li	a1, ARENA_HEIGHT
	 li	a2, ARENA_WIDTH
	 jal	 print_scoreboard
	 li	a0, 0
	 li	a1, 0
	 li	a2, 0
	 jal	 initialize_board
	 li	a0, 2
	 li	a1, 0
	 jal	print_arena_walls
	 
	 
	
	 
	 #Draw enemy
	 la	a0, enemy_struct_1
	 la	a1, enemy_sprite
	 jal	draw_enemy
	 la	a0, enemy_struct_2
	 la	a1, enemy_sprite
	 jal	draw_enemy
	  la	a0, enemy_struct_3
	 la	a1, enemy_sprite
	 jal	draw_enemy
	  la	a0, enemy_struct_4
	 la	a1, enemy_sprite
	 jal	draw_enemy
	  la	a0, enemy_struct_5
	 la	a1, enemy_sprite
	 jal	draw_enemy
	 
	 la	a0, player_struct
	 la	a1, player_sprite
	 la	a2, player_sprite_blank
	 jal	draw_player
	
	lb	t0, game_over
	beq	t0, 1, _game_over
	
	lb	t0, enemies_left
	beq	t0, 0, _game_over
	jal	display_update_and_clear
	
	
	## This function will block waiting for the next frame!
	jal	wait_for_next_frame
	b	_game_loop

_game_over:
	lb	t0, enemies_left
	beq	t0, 0, player_won
	
	jal	display_update_and_clear
	jal	wait_for_next_frame
	li	a0, 8
	li	a1, 26
	la	a2, you_lost_string
	jal	display_draw_text
	jal	display_update_and_clear
	j	game_exit

player_won:
	#jal	display_update_and_clear
	#jal	wait_for_next_frame
	li	a0, 8
	li	a1, 0
	la	a2, you_won_string
	jal	display_draw_text
	jal	display_update_and_clear
game_exit:
	exit



# --------------------------------------------------------------------------------------------------
# call once per main loop to keep the game running at 60FPS.
# if your code is too slow (longer than 16ms per frame), the framerate will drop.
# otherwise, this will account for different lengths of processing per frame.

wait_for_next_frame:
	enter	s0
	lw	s0, last_frame_time
_wait_next_frame_loop:
	# while (sys_time() - last_frame_time) < GAME_TICK_MS {}
	li	v0, 30
	syscall # why does this return a value in a0 instead of v0????????????
	sub	t1, a0, s0
	bltu	t1, GAME_TICK_MS, _wait_next_frame_loop

	# save the time
	sw	a0, last_frame_time

	# frame_counter++
	lw	t0, frame_counter
	inc	t0
	sw	t0, frame_counter
	leave	s0

# --------------------------------------------------------------------------------------------------
initialize_board:
	enter
	li	a0, 0
	li	a1, 0
	li	a2, BOARD_HEIGHT
	inc	a2
	li	a3, COLOR_BLUE
	jal	draw_vertical_line
	
	li	a0, 1
	li	a1, 0
	li	a2, BOARD_HEIGHT
	inc	a2
	li	a3, COLOR_BLUE
	jal	draw_vertical_line
	
	
	li	a0, 62
	li	a1 0
	li	a2, BOARD_HEIGHT
	inc	a2
	li	a3, COLOR_BLUE
	jal	draw_vertical_line
	
	li	a0, 63
	li	a1 0
	li	a2, BOARD_HEIGHT
	inc	a2
	li	a3, COLOR_BLUE
	jal	draw_vertical_line
	leave
#Description: Prints the Score area from (x,y) across the game board and draws the amount of lives left in the corner
#Inputs:
#a0=x
#a1=y
#a2 = Width of Board
print_scoreboard:
	enter s0, s1, s2, s4
	li	a3, COLOR_BLUE
	move	s0, a0
	move	s1, a1
	move	s2, a2
	li	s4, 0
score_loop:
	bge	s4, 2, setup_lives_loop
	inc	s1
	move	a0, s0
	move	a1, s1
	move	a2, s2
	li	a3, COLOR_BLUE
	jal	draw_horizontal_line
	inc	s4
	j	score_loop
	
	
	
setup_lives_loop:
	lb	s3, lives
	li	s0, 43
	li	s1, 58
	li	s4, 0 #reset counter for reuse in setup_lives
new_score_loop:
	bge	s4, s3, exit_scoreboard_func
	move	a0, s0
	move	a1, s1
	la	a2, player_sprite
	jal	display_blit_5x5_trans
	add	s0, s0, 6
	add	s4, s4, 1
	j	new_score_loop
exit_scoreboard_func:
	leave s0, s1, s2, s4

# void draw_horizontal_line(int x, int y, int size, int colour)
# for(i=0; i<size; i++) {
#   display_set_pixel(x+i, y, colour);
# }
draw_horizontal_line:
	# prologue
	enter	s0, s1, s2, s3, s4

	# Preserve all input parameters
	move	s0, a0					# s0 contains x
	move	s1, a1					# s1 contains y
	move	s2, a2					# s2 contains size
	move	s3, a3					# s3 contains colour

	li	s4, 0					# s4 contains i, initialize i=0
_draw_horizontal_line_loop:
	bge	s4, s2, _draw_horizontal_line_exit	# Check if i >= size
	# for loop implementation
	add	a0, s0, s4				# Calculate x-coordinate = x+i
	move	a1, s1					# Calculate y-coordinate is fixed
	move	a2, s3					# Colour set by user
	jal	display_set_pixel
	inc	s4					# Increment i
	j	_draw_horizontal_line_loop
_draw_horizontal_line_exit:
	# epilogue
	leave	s0, s1, s2, s3, s4


#Description: Draws a vertical line equal to the size specified with color 'col' from (x,y)  to (x, y + (size-1))
#Inputs:
#a0 = x
#a1 = y
#a2 = size
#a3 = color
#Returns: none
draw_vertical_line:
	enter s0, s1, s2, s3, s4
	move	s0, a0 #x
	move	s1, a1 #y
	move	s2, a2 #size
	move	s3, a3 #color
	sub	s2, s2, 1 # size -= 1
draw_vertinel_line_loop:
	ble	s2, 0,  draw_vertical_line_exit
	move	a0, s0
	move	a1, s1
	move	a2, s3
	jal	display_set_pixel
	add	s1, s1, 1
	sub	s2, s2, 1
	j	draw_vertinel_line_loop
draw_vertical_line_exit:
	leave s0, s1, s2, s3, s4

#Description: Prints the arena walls according to the arena variable using the wallblock from (x,y) all the way to the end of the arena
#Inputs:
#a0= x
#a1= y
#How to do: Iterate over all values in arena. Where ever there is a 1 in the arena, blit_5x5.
		#First have for loop that iterates over the y values 
		#In each iteration of the y values, have a for loop that iterates over the x values in the matrix
print_arena_walls:
	enter s0, s1, s2, s3, s4, s5
	#Need code to iterate over matrix
	la	s0, arena #base address storage
	li	s1, 0 #counter
	li	s2, 0 #I counter
	li	s3, 0 #J counter
	move	s4, a0#display x
	move	s5, a1#display y
main_iterator:
	beq	s1, ARENA_SIZE, exit_arena_walls #132
	li	t0, 0
	mul	t0, s2, ARENA_BLOCK_WIDTH#t0 = I*WIDTH
	add	t0, t0, s3 #t0 = t0 + J
	add	t0, t0, s0#t0 = I*WIDTH + J + Base
	#do stuff with display here
	#Testing stuff
	lb	t1, (t0)
	#move	a0, t1
	#li	v0, 1
	#syscall

	beq	t1, 0, not_wall
	move	a0, s4
	move	a1, s5
	la	a2, wallblock
	jal	display_blit_5x5
not_wall:
	bge	s4, 56, display_new_line
	add	s4, s4, 5
	j	check_matrix
display_new_line:
	li	s4, 2
	add	s5, s5, 5
check_matrix:
	beq	s3, 11,	reset_matrix_j
	add	s3, s3, 1#increment J
	add	s1, s1, 1
	j	main_iterator
	
reset_matrix_j:
	add	s2, s2, 1
	li	s3, 0
	add	s1, s1, 1
	j	main_iterator
exit_arena_walls: 
	leave s0, s1, s2, s3, s4, s5


#Description: If either player lives are 0 or enemies_left is 0, then quit the game
check_for_over:
	enter s0, s1, s2
	lb	s0, game_over
	beq	s0, 1, player_lost
	j	leave_check_for_over
player_lost:
	
leave_check_for_over:	
	leave s0, s1, s2

# la	s0, player_struct
	# add	s1, s0, x_pos
	# add	s2, s0, y_pos
	# lw	a0, (s1)
	# lw	a1, (s2)
	# la	a2, player_sprite
	# jal	display_blit_5x5_trans
