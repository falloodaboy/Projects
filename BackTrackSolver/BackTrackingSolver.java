public class BackTrackingSolver {

	private SolutionInterface solution;
	private DecisionInterface decision;

	public BackTrackingSolver(SolutionInterface solution, DecisionInterface decision) {
		this.solution = solution;
		this.decision = decision;
	}
	public SolutionInterface solve() {
		return solve(decision, solution);
	}

	private  SolutionInterface solve(DecisionInterface currentDecision, SolutionInterface partial) {
		SolutionInterface result = null;
		//no more decisions?
		if(currentDecision.isTerminalDecision()) {
			//System.out.println(partial);
			return partial;
		}

		//iterate over all options for currentDecision

		while(currentDecision.hasNextOption()) {
			//advance to next option
			currentDecision.nextOption();
			//is next option feasible?
			if(partial.isFeasible(currentDecision)) {
				//apply the option to the partial solution
				partial.applyOption(currentDecision);
				//dispatch a worker to examine the option
				result  = solve(currentDecision.getNextDecision(), partial);
				//check the worker's output
				if(result != null) //found a solution?
					return result;
				//undo the option from the partial solution
				partial.undoOption(currentDecision);
			}
		}
		//all options have been exhausted without finding a solution
		//return null
		assert result == null;
		return null;
	}
}
