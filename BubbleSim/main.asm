.include "convenience.asm"
.include "game_settings.asm"
.data
	braker: .asciiz "\n"
	
.text
.globl main

main:
	# set up anything you need to here,
	# and wait for the user to press a key to start.
        jal     game
	exit

	# li	a0, 7
	# jal	find_block_row
	 #move	s0, v0
	# li	a0, 12
	# jal	find_block_col
	# move	s1, v0
	# li	a0, 7
	#li	a1, 12
	# la	a2, arena
	# jal	check_location
	# move	a0, v0
	# li	v0, 1
	# syscall
	# la	s2, arena
	#mul	s3, s0, ARENA_BLOCK_WIDTH #s3 = I*WIDTH
	# add	s3, s3, s1 #s3 = I*WIDTH + J
	# add	s3, s3, s2# s3 = I*WIDTH + J + Base
	# lb	a0, (s3)
	#li	v0, 1
	# syscall