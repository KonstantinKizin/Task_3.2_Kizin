package by.online.pharmacy.controller.command;

import by.online.pharmacy.controller.exception.ControllerException;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import static by.online.pharmacy.controller.constant.ControllerConstant.WebProperty;

public class CommandProvider implements Command {

    private final Map<String , Command> commandMap = new HashMap<>();;

    @Override
    public CommandReturnObject execute(HttpServletRequest request) throws ControllerException {

        String commandName = request.getParameter(WebProperty.HIDDEN_PARAMETER);
        Command command = commandMap.get(commandName);
        return command.execute(request);
    }

    public Map<String, Command> getCommandMap() {
        return commandMap;
    }




}
