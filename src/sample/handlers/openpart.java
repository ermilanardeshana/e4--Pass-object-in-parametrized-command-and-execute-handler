
package sample.handlers;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.eclipse.core.commands.ParameterType;
import org.eclipse.core.commands.ParameterValueConversionException;
import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MBasicFactory;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.EPartService.PartState;


public class openpart {

	@Inject
	EPartService partService;
	MApplication application;
	EModelService modelService;
	@Inject ECommandService  commandService;
	
	private String S_CMD_MY_COMMAND_ID_PARAMETER ="PartName";
	private String S_CMD_MY_COMMAND_ID = "sample.command.openpart";
	/**
	 * This method open a view in new part stack 
	 * 
	 * @param commandParameters
	 * @param partService
	 * @param application
	 * @param modelService
	 * @throws CoreException
	 */
	@Execute
	public void execute(ParameterizedCommand commandParameters, EPartService partService,MApplication application,EModelService modelService,ECommandService commandService) throws CoreException {
		this.commandService = commandService;
		if(null == commandParameters){
			return;
		}
		Map<String, Object> parameterMap = commandParameters.getParameterMap();
		boolean partExist = false;
		Object convertParameter = convertParameter(commandParameters , S_CMD_MY_COMMAND_ID_PARAMETER);
		if(!partExist){
			MPart part = MBasicFactory.INSTANCE.createPart();
			part.setLabel(parameterMap.get("PartName").toString());
			part.setElementId("sample.part.samplepart");
			part.setCloseable(true);
			part.setToBeRendered(true);
			part.setContributionURI("bundleclass://sample/sample.parts.SamplePart");
			part.setContainerData("100");
			List<MPartStack> stacks = modelService.findElements(application, null,  MPartStack.class, null);
			stacks.get(1).getChildren().add(part);
			stacks.get(1).setVisible(true);
			partService.showPart(part, PartState.ACTIVATE);
			partService.activate(part, true);
		}

	}
	
	/**
	 *  Converts the String to your custom object
	 * @param command
	 * @param parameterId
	 * @return
	 */
	public Object convertParameter(ParameterizedCommand command, String parameterId) {
		String parameterValue = (String) command.getParameterMap().get(parameterId);
		org.eclipse.core.commands.Command c = commandService.getCommand(S_CMD_MY_COMMAND_ID);
		Object result = null;
		ParameterType type;
		try {
			type = c.getParameterType(parameterId);
			result = type.getValueConverter().convertToObject(parameterValue);
		} catch (ParameterValueConversionException e) {
			e.printStackTrace();
		} catch (NotDefinedException e) {
			e.printStackTrace();
		}
		return result;
	}

}