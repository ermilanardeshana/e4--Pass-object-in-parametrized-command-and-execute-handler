 
package sample.handlers;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.eclipse.core.commands.AbstractParameterValueConverter;
import org.eclipse.core.commands.ParameterType;
import org.eclipse.core.commands.ParameterValueConversionException;
import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.core.commands.EHandlerService;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.workbench.UIEvents.Command;

/**
 * This class is useful to create a parametrized command in e4 application 
 * @author Milan
 *
 */
public class openpartcommandexecutor {
	@Inject ECommandService  commandService;
	@Inject EHandlerService service;
	private String S_CMD_MY_COMMAND_ID = "sample.command.openpart";
	
	@Execute
	public void execute() {
		try{
		 Map<String, Object> parameters = new HashMap<String, Object>();
		 final org.eclipse.core.commands.Command command = commandService.getCommand(S_CMD_MY_COMMAND_ID);
         String value = "my sample part";
         parameters.put("PartName", value);
         ParameterizedCommand myCommand = commandService.createCommand(S_CMD_MY_COMMAND_ID , parameters);
         service.activateHandler(S_CMD_MY_COMMAND_ID, new openpart());
         if (!service.canExecute(myCommand))
                return;
         service.executeHandler(myCommand);// this will execute your handler as mentioned in part 2
		}catch (Exception ex) {
         ex.printStackTrace();
  }

		
	}
		
}