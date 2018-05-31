package io.wexchain.dcc.marketing.domainservice.function.command;

public class CommandAndContent<CMD, CNT> {
	private CMD command;
	private CNT content;

	public CommandAndContent() {
	}

	public CommandAndContent(CMD command, CNT content) {
		this.command = command;
		this.content = content;
	}

	public CMD getCommand() {
		return command;
	}

	public void setCommand(CMD command) {
		this.command = command;
	}

	public CNT getContent() {
		return content;
	}

	public void setContent(CNT content) {
		this.content = content;
	}

}
