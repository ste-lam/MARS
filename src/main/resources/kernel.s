	.kdata
__KERNEL_SYSCALL_TABLE__:
	.include "kernel_syscalls.s"
__KERNEL_SYSCALL_TABLE_END__:

################ ENABLE  DELAYED BRANCHING #############
	.ktext 0x80000180

__KERNEL_START__:
	mfc0 $k0, $13		# Cause register
	andi $k0, $k0, 0x7C	# Extract ExcCode Field * 4

	beq $k0, 32, __KERNEL_SYSCALL_HANDLER__
	nop
	eret

__KERNEL_SYSCALL_HANDLER__:
	# load syscall function address
	sltiu $k0, $v0, (__KERNEL_SYSCALL_TABLE_END__ - __KERNEL_SYSCALL_TABLE__)
	beq $k0, $zero, __KERNEL_ILLEGAL_SYSCALL__
	
	sll $k0, $v0, 2
	lw $k0, __KERNEL_SYSCALL_TABLE__($k0)
	beq $k0, $zero, __KERNEL_ILLEGAL_SYSCALL__
	move $k1, $ra		# copy userspace $ra
	
	jalr $k0		# call real service
	
__KERNEL_SYSCALL_END__:
	# syscall handlers will return to EPC + 4
	mfc0 $k0, $14		# EPC register
	addiu $k0, $k0, 4
	mtc0 $k0 $14

	move $ra, $k1		# restore userspace $ra

__KERNEL_END__:
	eret

__KERNEL_ILLEGAL_SYSCALL__:
	li $v0, -1000		# unknown syscall error
	j __KERNEL_SYSCALL_END__
	move $k1, $ra		# copy userspace $ra