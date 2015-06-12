package android.emu6502

import android.emu6502.instructions.BaseInstruction
import android.emu6502.instructions.Instruction
import android.emu6502.instructions.InstructionTarget
import android.emu6502.instructions.impl.*
import android.util.Log
import java.util.HashMap
import kotlin.reflect.KMemberFunction0

class CPU(private val memory: Memory) {
  // Accumulator
  var A: Int = 0
  // Registers
  var X: Int = 0
  var Y: Int = 0
  // Program counter
  var PC: Int = 0x600
  // Stack pointer
  var SP: Int = 0xFF
  // Processor flags
  var P: Int = 0
  private var isRunning = false
  private var debug = false
  private var monitoring = false
  private var TAG = "CPU"
  val instructionList: HashMap<Int, InstructionTarget> = HashMap()
  private val operationList: HashMap<Instruction, BaseInstruction> = hashMapOf(
      Pair(Instruction.ADC, ADC(this)),
      Pair(Instruction.AND, AND(this)),
      Pair(Instruction.ASL, ASL(this)),
      Pair(Instruction.BIT, BIT(this)),
      Pair(Instruction.LDA, LDA(this)),
      Pair(Instruction.LDX, LDX(this)),
      Pair(Instruction.LDY, LDY(this)),
      Pair(Instruction.STA, STA(memory, this)),
      Pair(Instruction.STX, STX(this)),
      Pair(Instruction.ORA, ORA(this))
//      Pair(Instruction.BPL, BPL(this)),
//      Pair(Instruction.BMI, BMI(this)),
//      Pair(Instruction.BVC, BVC(this)),
//      Pair(Instruction.BVS, BVS(this)),
//      Pair(Instruction.BCC, BCC(this)),
//      Pair(Instruction.BCS, BCS(this)),
//      Pair(Instruction.BNE, BNE(this)),
//      Pair(Instruction.BEQ, BEQ(this)),
//      Pair(Instruction.BRK, BRK(this)),
//      Pair(Instruction.CMP, CMP(this)),
//      Pair(Instruction.CPX, CPX(this)),
//      Pair(Instruction.CPY, CPY(this)),
//      Pair(Instruction.DEC, DEC(this)),
//      Pair(Instruction.EOR, EOR(this)),
//      Pair(Instruction.CLC, CLC(this)),
//      Pair(Instruction.SEC, SEC(this)),
//      Pair(Instruction.CLI, CLI(this)),
//      Pair(Instruction.SEI, SEI(this)),
//      Pair(Instruction.CLV, CLV(this)),
//      Pair(Instruction.CLD, CLD(this)),
//      Pair(Instruction.SED, SED(this)),
//      Pair(Instruction.INC, INC(this)),
//      Pair(Instruction.JMP, JMP(this)),
//      Pair(Instruction.JSR, JSR(this)),
//      Pair(Instruction.LSR, LSR(this)),
//      Pair(Instruction.NOP, NOP(this)),
//      Pair(Instruction.TAX, TAX(this)),
//      Pair(Instruction.TXA, TXA(this)),
//      Pair(Instruction.DEX, DEX(this)),
//      Pair(Instruction.INX, INX(this)),
//      Pair(Instruction.TAY, TAY(this)),
//      Pair(Instruction.TYA, TYA(this)),
//      Pair(Instruction.DEY, DEY(this)),
//      Pair(Instruction.INY, INY(this)),
//      Pair(Instruction.ROR, ROR(this)),
//      Pair(Instruction.ROL, ROL(this)),
//      Pair(Instruction.RTI, RTI(this)),
//      Pair(Instruction.RTS, RTS(this)),
//      Pair(Instruction.SBC, SBC(this)),
//      Pair(Instruction.TXS, TXS(this)),
//      Pair(Instruction.TSX, TSX(this)),
//      Pair(Instruction.PHA, PHA(this)),
//      Pair(Instruction.PLA, PLA(this)),
//      Pair(Instruction.PHP, PHP(this)),
//      Pair(Instruction.PLP, PLP(this)),
//      Pair(Instruction.STY, STY(this))
  )

  fun execute() {
    setRandomByte()
    executeNextInstruction()

    if (PC == 0 || !isRunning) {
      stop()
      Log.i(TAG, "Program end at PC=$" + (PC - 1))
    }
  }

  private fun stop() {
    isRunning = false
  }

  private fun executeNextInstruction() {
    val instruction = Integer.valueOf(popByte().toInt().toString(), 16)
    val target = instructionList.get(instruction)
    if (target != null) {
      val function = target.method
      target.operation.function()
    } else {
      Log.e(TAG, "Address $" + PC + " - unknown opcode")
    }
  }

   fun popByte(): Int {
    return memory.get((PC++).and(0xff));
  }

  private fun setRandomByte() {
    memory.set(0xfe, Math.floor(Math.random() * 256).toInt())
  }

  fun setSZFlagsForRegA() {
    setSZFlagsForValue(A)
  }

  private fun setSZFlagsForValue(value: Int) {
    if (value != 0) {
      P = P.and(0xfd);
    } else {
      P = P.or(0x02);
    }
    if (value.and(0x80) != 0) {
      P = P.or(0x80);
    } else {
      P = P.and(0x7f);
    }
  }

  fun popWord(): Int {
    return popByte() + popByte().shl(8)
  }
}