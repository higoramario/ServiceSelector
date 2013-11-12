package model;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
/*import samples.AbstractProblem;
import solver.Solver;
import solver.constraints.IntConstraintFactory;
import solver.search.strategy.IntStrategyFactory;
import solver.variables.IntVar;
import solver.variables.RealVar;
import solver.variables.VariableFactory;*/

public class CompositionChocoSolver{ //extends AbstractProblem{

	Composition composition;
	private Map<Integer,ArrayList<Service>> serviceMatrix;
	//Vars
	/*IntVar[] timeResponseVar;// = new IntVar[100];
	IntVar[] matrixTimeResponse;
	RealVar[] reliabilityVar;*/
	//Cons
	int compositionTimeResponse;
	float compositionReliability;
	int compositionLevel;
	
	
	//IntVar x,y,sum;
	
	public CompositionChocoSolver(Composition comp){
		composition = comp;
		compositionTimeResponse = composition.getResponseTime();
		compositionReliability = composition.getReliability();
		compositionLevel = composition.getServiceMatrix().size();
		
		//solver = new Solver("CompositionSolver");
	}
	
	//@Override
	public void buildModel() {
		parse();
		//solver.post(IntConstraintFactory.arithm(timeResponseVar[0],"+", timeResponseVar[1], "<", compositionTimeResponse));
		
	}

	//@Override
	public void configureSearch() {
		//solver.set(IntStrategyFactory.inputOrder_InDomainMin(timeResponseVar));
	}

	//@Override
	public void createSolver() {
		//solver = new Solver("CompositionSolver");
	}

	//@Override
	public void prettyOut() {
		// TODO Auto-generated method stub
		
	}

	//@Override
	public void solve() {
		//solver.findAllSolutions();
		
	}

	public void parse(){
		ArrayList<Service> serviceLevel;
		int level = 1;
		int numServ = 1;
		int countServ = 0;
		serviceMatrix = composition.getServiceMatrix();
		Set<Integer> keys = serviceMatrix.keySet();
		//serviceVar = new IntVar[serviceMatrix.size()];
		//timeResponseVar = new IntVar[4];
		//sum = VariableFactory.fixed(compositionTimeResponse, solver);

		for(int key: keys){
			serviceLevel = serviceMatrix.get(key);
			numServ = 1;
			
			for(Service serv : serviceLevel){
				//timeResponseVar[countServ] = VariableFactory.fixed("s_"+level+"_"+numServ, serv.getTime(), solver);
				countServ++;
				numServ++;
			}
			level++;
		}
	}
	
	public void printServices(){
		/*for(IntVar i: timeResponseVar){
			if (i != null)
				System.out.println(i.getName());
		}*/
	}
	
}
