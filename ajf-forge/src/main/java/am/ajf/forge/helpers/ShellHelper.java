package am.ajf.forge.helpers;

import javax.inject.Inject;

import org.jboss.forge.shell.Shell;

import am.ajf.forge.exceptions.EscapeForgePromptException;

public class ShellHelper {

	@Inject
	private Shell shell;

	public String promptFacade(String arg, String defaultValue)
			throws EscapeForgePromptException {

		String output = shell.prompt(arg, defaultValue);

		if ("exit".equals(output.toLowerCase())) {
			throw new EscapeForgePromptException();
		} else {
			return output;
		}

	}

	public String promptFacade(String arg) throws EscapeForgePromptException {

		String output = shell.prompt(arg);

		if ("exit".equals(output.toLowerCase())) {
			throw new EscapeForgePromptException();
		} else {
			return output;
		}
	}
}
