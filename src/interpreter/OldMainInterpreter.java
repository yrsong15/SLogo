//package interpreter;
//
//import java.lang.reflect.Constructor;
//import java.lang.reflect.InvocationTargetException;
//import java.lang.reflect.Method;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.LinkedList;
//import java.util.List;
//import java.util.MissingResourceException;
//import java.util.Queue;
//import java.util.ResourceBundle;
//
//
//import gui.SlogoCommandInterpreter;
//import regularExpression.ProgramParser;
//
//public class OldMainInterpreter implements SlogoCommandInterpreter {
//	
//	private final String DEFAULT_RESOURCE_LANGUAGE = "resources/languages/";
//	private final String DEFAULT_RESOURCE_PACKAGE = "resources/properties/";
//	private final String PROPERTIES_TITLE = "Interpreter";
//	private final String LISTOFSUBINTERPRETER_TITLE = "ListOfSubinterpreters";
//	private final double erroneousReturnValue = Double.NaN;
//	private String[] languages = {"English", "Syntax"};  //default language is English
//	
//	private ProgramParser lang;
//	private SlogoUpdate model;
//	private List listOfSubinterpreters;
//	private TurtleStateDataSource stateDatasource;
//	private TurtleStateUpdater stateUpdater;
//	private UserVariablesDataSource varDataSource;
//	private ErrorPresenter errorPresenter;
//	private ResourceBundle rb;
//	private Queue<String[]> listQueue;
//	private int currSearchIndex;
//	
//	private int repCount;
//	
//	public OldMainInterpreter(){
//		rb = ResourceBundle.getBundle(DEFAULT_RESOURCE_PACKAGE+PROPERTIES_TITLE);
//		listQueue = new LinkedList<String[]>();
//	}
//	
//	/**
//	 * Overload of parseInput() method that enables a global interpreter for multiple turtles
//	 */
// 	public void parseInput(String input, TurtleStateDataSource stateDataSource, TurtleStateUpdater stateUpdater, UserVariablesDataSource varDataSource, ErrorPresenter errorPresenter) throws ClassNotFoundException, NoSuchMethodException, 
// 	SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, 
// 	InvocationTargetException{
// 		this.stateDatasource = stateDataSource;
// 		this.stateUpdater = stateUpdater;
// 		this.varDataSource = varDataSource;
// 		this.errorPresenter = errorPresenter;
// 		this.parseInput(input);
// 	}
//	
//	public void parseInput(String input) throws ClassNotFoundException, NoSuchMethodException, 
//			SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, 
//			InvocationTargetException{
//		model = new SlogoUpdate(stateDatasource);
//		String[] split = input.split("\\s+");
//		lang = new ProgramParser();
//		lang = addLanguagePatterns(lang);	
////		for(String elem: split){
////			System.out.println(elem);
////		}
//		interpretCommand(split, 0);   //first search(non-recursive) begins at index 0;
//	}
//	
//	private double interpretCommand(String[] input, int searchStartIndex) throws ClassNotFoundException, NoSuchMethodException, 
//	SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
//		
//		String[] parsed = createParsedArray(input, lang);
//		
//		GeneralInterpreter decideCommand = new GeneralInterpreter();
//		String keyword = parsed[searchStartIndex].toLowerCase();
//
//		//scan for list first before anything else
//		searchForList(input, parsed);
//		
//		double returnValue = erroneousReturnValue; //initialized as an erroneous number
//		
//		if(decideCommand.isNonInputTurtleCommand(keyword) || decideCommand.isUnaryTurtleCommand(keyword) || 
//				decideCommand.isBinaryTurtleCommand(keyword)){
//			returnValue = interpretTurtleCommand(input, keyword, searchStartIndex);
//		}
//		
//		else if(decideCommand.isTurtleQuery(keyword)){
//			returnValue = interpretTurtleQuery(input, keyword, searchStartIndex);
//		}
//		
//		else if(decideCommand.isNonInputMathExpression(keyword) || decideCommand.isUnaryMathExpression(keyword) || 
//				decideCommand.isBinaryMathExpression(keyword)){
//			returnValue = interpretMathCommand(input, keyword, searchStartIndex);
//		}
//		// fd fd 50 rt 90
//		else if(decideCommand.isUnaryBooleanExpression(keyword) || decideCommand.isBinaryBooleanExpression(keyword)){
//			returnValue = interpretBooleanCommand(input, keyword, searchStartIndex);
//		}
//		
//		else if(decideCommand.isControl(keyword)){
//			returnValue = interpretControl(input, parsed, keyword, searchStartIndex);
//		}
//		
//		//if current keyword is a variable
//		else if(keyword.equalsIgnoreCase(rb.getString("VariableLabel"))){
//			String newKeyword = input[searchStartIndex].toLowerCase();
//			if(varDataSource.getUserDefinedVariable(newKeyword) != null){
//				returnValue = Double.parseDouble(varDataSource.getUserDefinedVariable(newKeyword));
//			}
//			else returnValue = 0;  //By definition, unassigned variables have a value of 0.
//		}	
//			
//		return getValueOfCommand(input, searchStartIndex, parsed, decideCommand, returnValue);
//	}
//
//	private double getValueOfCommand(String[] input, int searchStartIndex, String[] parsed,
//			GeneralInterpreter decideCommand, double returnValue) throws ClassNotFoundException, NoSuchMethodException,
//			InstantiationException, IllegalAccessException, InvocationTargetException {
//		
//		if(returnValue != erroneousReturnValue){
//			if(hasRemainingActionable(parsed, decideCommand, currSearchIndex) < 0){
//				stateUpdater.applyChanges(model);
//				return returnValue;
//			}
//			else{
//				stateUpdater.applyChanges(model);
//				return interpretCommand(input, currSearchIndex+1);
//			}
//		}
//		else{
//			String errorMessage = "Invalid argument detected: '"+input[searchStartIndex]+"' is not a valid command!";
//			System.out.println(errorMessage);
//			errorPresenter.presentError(errorMessage);
//			throw new IllegalArgumentException();
//		}
//	}
//
//	private void searchForList(String[] input, String[] parsed) {
//		for(int i=0;i<parsed.length;i++){
//			String keyword = parsed[i];
//			if(keyword.equalsIgnoreCase(rb.getString("ListStartLabel"))){
//				int temp = i+1;
//				int listStartIndex = temp;
//				while(!parsed[temp].equalsIgnoreCase(rb.getString("ListEndLabel"))){	
//					temp++;
//				}
//				int listEndIndex = temp;  //temp is currently at index of ']'.
//				listQueue.add(Arrays.copyOfRange(input, listStartIndex, listEndIndex));
//			}
//		}	
//	}
//	
//	private double interpretTurtleCommand(String[] input, String keyword, int searchStartIndex) throws ClassNotFoundException, 
//	InstantiationException, IllegalAccessException, NoSuchMethodException, SecurityException, 
//	IllegalArgumentException, InvocationTargetException{
//		double[] param;
//		Class interpreterClass = Class.forName(rb.getString("TurtleCommandInterpreterLabel"));
//		Object obj = interpreterClass.getDeclaredConstructor(SlogoUpdate.class, TurtleStateUpdater.class)
//				.newInstance(model, stateUpdater);
//		Class[] args;
//		TurtleCommandInterpreter interpreter = new TurtleCommandInterpreter(model, stateUpdater);
//		
//		if(interpreter.isNonInputTurtleCommand(keyword)){
//			return handleNonInputKeywordWithModel(keyword, searchStartIndex, interpreterClass, obj, interpreter);
//		}
//		else if(interpreter.isUnaryTurtleCommand(keyword)){
//			return handleUnaryKeywordWithModel(input, keyword, searchStartIndex, interpreterClass, obj, interpreter);
//		}
//		else if(interpreter.isBinaryTurtleCommand(keyword)){
//			return handleBinaryKeywordWithModel(input, keyword, searchStartIndex, interpreterClass, obj, interpreter);
//		}
//		else throw new IllegalArgumentException();
//	}
//
//	
//	private double interpretTurtleQuery(String[] input, String keyword, int searchStartIndex) throws ClassNotFoundException, 
//	InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException{
//		Class interpreterClass = Class.forName(rb.getString("TurtleQueryInterpreterLabel"));
//		Object obj = interpreterClass.getDeclaredConstructor(SlogoUpdate.class).newInstance(model);
//		TurtleCommandInterpreter interpreter = new TurtleCommandInterpreter(model, stateUpdater);
//		if(interpreter.isTurtleQuery(keyword)){
//			return handleNonInputKeywordWithModel(keyword, searchStartIndex, interpreterClass, obj, interpreter);
//		}
//		else throw new IllegalArgumentException();
//	}
//	
//	private double interpretMathCommand(String[] input, String keyword, int searchStartIndex) throws ClassNotFoundException, InstantiationException, 
//	IllegalAccessException, NoSuchMethodException, SecurityException, IllegalArgumentException, InvocationTargetException{
//		Class interpreterClass = Class.forName(rb.getString("MathInterpreterLabel"));
//		Object obj = interpreterClass.newInstance();
//		MathInterpreter interpreter = new MathInterpreter();
//		
//		if(interpreter.isNonInputMathExpression(keyword)){
//			return handleNonInputKeyword(keyword, searchStartIndex, interpreterClass, obj);
//		}
//		else if(interpreter.isUnaryMathExpression(keyword)){
//			return handleUnaryKeyword(input, keyword, searchStartIndex, interpreterClass, obj);
//		}
//		else if(interpreter.isBinaryMathExpression(keyword)){
//			return handleBinaryKeyword(input, keyword, searchStartIndex, interpreterClass, obj);
//		}
//		else throw new IllegalArgumentException();
//	}
//	
//	private double interpretBooleanCommand(String[] input, String keyword, int searchStartIndex) throws ClassNotFoundException, InstantiationException, 
//	IllegalAccessException, NoSuchMethodException, SecurityException, IllegalArgumentException, InvocationTargetException{
//		Class interpreterClass = Class.forName(rb.getString("BooleanInterpreterLabel"));
//		Object obj = interpreterClass.newInstance();
//		BooleanInterpreter interpreter = new BooleanInterpreter();
//
//		if(interpreter.isUnaryBooleanExpression(keyword)){
//			return handleUnaryKeyword(input, keyword, searchStartIndex, interpreterClass, obj);
//		}
//		else if(interpreter.isBinaryBooleanExpression(keyword)){
//			return handleBinaryKeyword(input, keyword, searchStartIndex, interpreterClass, obj);
//		}
//		else throw new IllegalArgumentException();
//	}
//	
//	private double interpretControl(String[] input, String[] parsed, String keyword, int searchStartIndex) throws ClassNotFoundException, 
//	NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, 
//	IllegalArgumentException, InvocationTargetException{
//		double[] param;
//		if(keyword.equalsIgnoreCase(rb.getString("makevar"))){
//			param = parseParam(input, searchStartIndex+2, 1);
//			currSearchIndex = searchStartIndex+2;
//			if(parsed[searchStartIndex+1].equalsIgnoreCase(rb.getString("VariableLabel"))){
//				varDataSource.addUserDefinedVariable(input[searchStartIndex+1], Double.toString(param[0]));
//				System.out.println(param[0]);
//				return param[0];
//			}
//			else{
//				String errorMessage = "Illegal Variable detected: '" + input[searchStartIndex+1] + "' is not a variable!";
//				System.out.println(errorMessage);
//				errorPresenter.presentError(errorMessage);
//				throw new IllegalArgumentException();
//			}
//		}
//		else if(keyword.equalsIgnoreCase(rb.getString("repeat"))){
//			repCount = 0;
//			param = parseParam(input, searchStartIndex+1, 1);
//			double res = 0;
//			String[] temp = listQueue.peek();
//			for(int i=0;i<param[0];i++){
//				res = interpretCommand(temp, 0);
//				repCount++;
//			}
//			listQueue.remove();
//			System.out.println(res);
//			currSearchIndex = searchStartIndex+2;
//			return res;
//		}
//		
//		//TODO: Implement other controls other than set and repeat
//		else return 0;
//	}
//	
//	private double handleNonInputKeywordWithModel(String keyword, int searchStartIndex, Class interpreterClass, Object obj,
//			TurtleCommandInterpreter interpreter)
//			throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
//		double res = helpHandleNonInputKeyword(keyword, searchStartIndex, interpreterClass, obj);
//		model = interpreter.getModel();
//		return res;
//	}
//
//	private double handleUnaryKeywordWithModel(String[] input, String keyword, int searchStartIndex,
//			Class interpreterClass, Object obj, TurtleCommandInterpreter interpreter) throws ClassNotFoundException,
//			NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
//		double res = helpHandleUnaryKeyword(input, keyword, searchStartIndex, interpreterClass, obj);
//		model = interpreter.getModel();
//		return res;
//	}
//	
//	private double handleBinaryKeywordWithModel(String[] input, String keyword, int searchStartIndex,
//			Class interpreterClass, Object obj, TurtleCommandInterpreter interpreter) throws ClassNotFoundException,
//			NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
//		double res = helpHandleBinaryKeyword(input, keyword, searchStartIndex, interpreterClass, obj);
//		model = interpreter.getModel();
//		return res;
//	}
//	
//	private double handleNonInputKeyword(String keyword, int searchStartIndex, Class interpreterClass, Object obj)
//			throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
//		double res = helpHandleNonInputKeyword(keyword, searchStartIndex, interpreterClass, obj);
//		return res;
//	}
//
//	private double handleBinaryKeyword(String[] input, String keyword, int searchStartIndex, Class interpreterClass,
//			Object obj) throws ClassNotFoundException, NoSuchMethodException, InstantiationException,
//			IllegalAccessException, InvocationTargetException {
//		double res = helpHandleBinaryKeyword(input, keyword, searchStartIndex, interpreterClass, obj);
//		return res;
//	}
//
//	private double handleUnaryKeyword(String[] input, String keyword, int searchStartIndex, Class interpreterClass,
//			Object obj) throws ClassNotFoundException, NoSuchMethodException, InstantiationException,
//			IllegalAccessException, InvocationTargetException {
//		double res = helpHandleUnaryKeyword(input, keyword, searchStartIndex, interpreterClass, obj);
//		return res;
//	}
//
//	private double helpHandleNonInputKeyword(String keyword, int searchStartIndex, Class interpreterClass, Object obj)
//			throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
//		currSearchIndex = searchStartIndex;
//		Class[] args;
//		args = createDoubleArgs(0);
//		Method method = interpreterClass.getDeclaredMethod(keyword, args);
//		double res = (double) method.invoke(obj);
//		System.out.println(res);
//		return res;
//	}
//
//	private double helpHandleBinaryKeyword(String[] input, String keyword, int searchStartIndex, Class interpreterClass,
//			Object obj) throws ClassNotFoundException, NoSuchMethodException, InstantiationException,
//			IllegalAccessException, InvocationTargetException {
//		currSearchIndex = searchStartIndex+2;
//		double[] param;
//		Class[] args;
//		param = parseParam(input, searchStartIndex+1, 2);
//		args = createDoubleArgs(2);
//		Method method = interpreterClass.getDeclaredMethod(keyword, args);
//		double res = (double) method.invoke(obj, param[0], param[1]);
//		System.out.println(res);
//		return res;
//	}
//	
//	private double helpHandleUnaryKeyword(String[] input, String keyword, int searchStartIndex, Class interpreterClass,
//			Object obj) throws ClassNotFoundException, NoSuchMethodException, InstantiationException,
//			IllegalAccessException, InvocationTargetException {
//		currSearchIndex = searchStartIndex+1;
//		double[] param;
//		Class[] args;
//		param = parseParam(input, searchStartIndex+1, 1);
//		args = createDoubleArgs(1);
//		Method method = interpreterClass.getDeclaredMethod(keyword, args);
//		double res = (double) method.invoke(obj, param[0]);
//		System.out.println(res);
//		return res;
//	}
//
//	
//	private double[] parseParam(String[] input, int startSearchIndex, int numOfParams) throws ClassNotFoundException, 
//	NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, 
//	IllegalArgumentException, InvocationTargetException{
//		double[] res = new double[numOfParams];
//		int index=0;
//		for(int i=startSearchIndex;i<startSearchIndex+numOfParams;i++){
//			if(isDouble(input[i])){
//				double temp = Double.parseDouble(input[i]);
//				res[index++] = temp;
//			}
//			else{
//				//recursive parsing of input statement
//				res[index++] = interpretCommand(input, i);
//			}
//		}
//		return res;
//	}
//	
//	private List<SubInterpreter> createListOfInterpreters() throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
//		List<SubInterpreter> list = new ArrayList<SubInterpreter>();
//		ResourceBundle reader = 
//				ResourceBundle.getBundle(DEFAULT_RESOURCE_PACKAGE+LISTOFSUBINTERPRETER_TITLE);
//		for(String elem: reader.keySet()){
//			Class<?> clazz = Class.forName(elem);
//			Constructor<?> constructor = clazz.getConstructor(String.class, Integer.class);
//			SubInterpreter instance = (SubInterpreter)constructor.newInstance(model, stateUpdater);
//			list.add(instance);
//		}
//		
//		return list;
//	}
//	
//	private String[] createParsedArray(String[] in, ProgramParser lang){
//		String[] out = new String[in.length];
//		for(int i=0;i<in.length;i++){
//			out[i] = lang.getSymbol(in[i]);
//		}
//		return out;
//	}
//	
//	private ProgramParser addLanguagePatterns(ProgramParser lang){
//		for(String language:languages){
//			lang.addPatterns(DEFAULT_RESOURCE_LANGUAGE+language);
//		}
//        return lang;
//	}
//	
//	private Class[] createDoubleArgs(int num){
//		Class[] args = new Class[num];
//		for(int i=0;i<args.length;i++){
//			args[i] = Double.TYPE;
//		}
//		return args;
//	}
//
//	private boolean isDouble(String str) {
//		try {
//			Double.parseDouble(str);
//			return true;
//		} catch (NumberFormatException e) {
//			return false;
//		}
//	}
//	
//	/**
//	 * Returns -1 if no remaining Turtle Commands, otherwise returns index of next Turtle Command
//	 */
//	private int hasRemainingActionable(String[] parsed, GeneralInterpreter interpreter, int index){
//		int resIndex = -1;
//		if(index == parsed.length || parsed[index].equalsIgnoreCase(rb.getString("ListStartLabel"))) return resIndex;
//		for(int i=index+1;i<parsed.length;i++){
//
//			if(interpreter.isBinaryTurtleCommand(parsed[i]) || interpreter.isUnaryTurtleCommand(parsed[i]) ||
//				interpreter.isNonInputTurtleCommand(parsed[i])){
//				resIndex = i;
//				break;
//			}
//		}
//		return resIndex;
//	}
//	
//	public void setLanguage(String language){
//		ProgramParser checkLang = new ProgramParser();
//		try{
//			checkLang.addPatterns(DEFAULT_RESOURCE_LANGUAGE+language);
//			String[] temp = {language, "Syntax"};
//			languages = temp;
//			lang = checkLang;
//		} catch(MissingResourceException e){
//			String langErrorMessage = language+" is not a valid language!\n Set to English by default.";
//			errorPresenter.presentError(langErrorMessage);
//			System.out.println(langErrorMessage);
//		}
//
//	}
//	
//	public void setStateDataSource(TurtleStateDataSource stateDataSource){
//		this.stateDatasource = stateDataSource;
//	}
//	
//	public void setStateUpdater(TurtleStateUpdater stateUpdater){
//		this.stateUpdater = stateUpdater;
//	}
//	
//	public void setVarDataSource(UserVariablesDataSource varDataSource){
//		this.varDataSource = varDataSource;
//	}
//	
//	public void setErrorPresenter(ErrorPresenter p){
//		this.errorPresenter = p;
//	}
//	
//	public int getRepCount(){
//		return repCount;
//	}
//}