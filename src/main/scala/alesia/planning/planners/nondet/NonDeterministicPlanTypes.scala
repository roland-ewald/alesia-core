package alesia.planning.planners.nondet

/**
 * Types of non-deterministic plans.
 *
 * Basically, strong plans always work, strong cyclic plans always work when the state transitions are fair
 * (i.e. in a non-deterministic setting, a transition will not always 'win' against other alternatives), and
 * weak plans *may* work.
 *
 * See p. 408 of
 *
 * M. Ghallab, D. Nau, and P. Traverso, Automated Planning: Theory & Practice, 1st ed., ser. The Morgan Kaufmann Series in Artificial Intelligence.
 *
 * for a more formal introduction.
 *
 * @author Roland Ewald
 */
object NonDeterministicPlanTypes extends Enumeration {
  val Strong, StrongCyclic, Weak = Value
}