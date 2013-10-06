package alesia.results

/**
 * Implementations of [[PlanExecutionResultAnalyzer]] that return new results for simulator performance prediction.
 * This is useful for an automated feedback to a sub-system for algorithm selection.
 *
 * TODO: This is just a sketch...
 *
 * @author Roland
 */
trait AlgorithmPerformanceResultAnalyzer[A <: PerformanceResult] extends PlanExecutionResultAnalyzer[Seq[A]]

/** Represents the result of a performance analysis*/
sealed trait PerformanceResult

/** A new performance estimator. */
case class PerformanceEstimator() extends PerformanceResult

/** The invalidation of some performance estimator. */
case class PerformanceEstimatorInvalidation() extends PerformanceResult