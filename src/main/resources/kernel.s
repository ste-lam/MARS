	.kdata 
#__CAUSE_TABLE__:
#	.word __Int__                         #  0 - Interrupt
#	.word __Mod__                         #  1 - TLB Modification exception
#	.word __TLBL__                        #  2 - TLB Miss exception (load or instruction fetch)
#	.word __TLBS__                        #  3 - TLB Miss exception (store)
#	.word __AdEL__                        #  4 - Address Error exception (load or instruction fetch)
#	.word __AdES__                        #  5 - Address Error exception (store)
#	.word __IBE__                         #  6 - Bus Error exception (instruction fetch)
#	.word __DBE__                         #  7 - Bus Error exception (data reference: load or store)
#	.word __Sys__                         #  8 - Syscall exception
#	.word __Pb__                          #  9 - Breakpoint exception
#	.word __RI__                          # 10 - Reserved Instruction exception
#	.word __CpU__                         # 11 - Coprocessor Unusable exception
#	.word __Ov__                          # 12 - Arithmetic Overflow exception
#	.word __Tr__                          # 13 - Trap exception
#	.word __MSAFPE__                      # 14 - MSA Floating-Point exception
#	.word __FPE__                         # 15 - Floating-Point exception
#	.word 0                               # 16 - Available for implementation-dependent use
#	.word 0                               # 17 - Available for implementation-dependent use
#	.word __C2E__                         # 18 - Reserved for precise Coprocessor 2 exceptions
#	.word __TLBRI__                       # 19 - TLB Read-Inhibit exception
#	.word __TLBXI__                       # 20 - TLB Execution-Inhibit exception
#	.word __MSADis__                      # 21 - MSA Disabled exception
#	.word __MDMX__                        # 22 - Previously MDMX Unusable Exception (MDMX ASE). MDMX deprecated with Revision 5.
#	.word __WATCH__                       # 23 - Reference to WatchHi/WatchLo address
#	.word __MCheck__                      # 24 - Machine check
#	.word __Thread__                      # 25 - Thread Allocation, Deallocation, or Scheduling Exceptions (MIPS® MT Module)
#	.word __DSPDis__                      # 26 - DSP Module State Disabled exception (MIPS® DSP Module)
#	.word __GE__                          # 27 - Virtualized Guest Exception
#	.word 0                               # 28 - Reserved
#	.word 0                               # 29 - Reserved
#	.word __CacheErr__                    # 30 - Cache error. In normal mode, a cache error exception has a dedicated vector and the Cause register is not updated.
#	.word 0                               # 31 - Reserved

#_s1:	.word 0
#_s2:	.word 0

.include "kernel_syscalls.s"


################ ENABLE  DELAYED BRANCHING #############
	.ktext 0x80000180

__KERNEL_START__:
	mfc0 $k0, $13		# Cause register
	andi $k0, $k0, 0x7C	# Extract ExcCode Field * 4

	# syscall handlers will return to EPC + 4
	bne $k0, 32, __KERNEL_END__
	nop
	mfc0 $k0, $14		# EPC register
	addiu $k0, $k0, 4

__KERNEL_SYSCALL_HANDLER__:
	# load syscall function address
	sltiu $k1, $v0, __KERNEL_SYSCALL_TABLE_SIZE__
	beq $k1, $zero, __KERNEL_ILLEGAL_SYSCALL__
	sll $k1, $v0, 2

	lw $k1, __KERNEL_SYSCALL_TABLE__($k1)
	beq $k1, $zero, __KERNEL_ILLEGAL_SYSCALL__
	nop

	jalr $k1
	nop

__KERNEL_ILLEGAL_SYSCALL__:

	
__KERNEL_END__:
	mtc0 $k0 $14
	eret

