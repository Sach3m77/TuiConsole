package pl.projekt.tui.ssh;

import org.apache.sshd.server.channel.ChannelSession;
import org.apache.sshd.server.command.Command;
import org.apache.sshd.server.shell.ShellFactory;

class ClientSSHShellFactory implements ShellFactory {

    @Override
    public Command createShell(ChannelSession channel) {
        return new ClientSSHHandler();
    }
}
