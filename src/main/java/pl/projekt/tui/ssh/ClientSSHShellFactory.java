package pl.projekt.tui.ssh;

import org.apache.sshd.server.channel.ChannelSession;
import org.apache.sshd.server.command.Command;
import org.apache.sshd.server.shell.ShellFactory;

/**
 * Factory class for creating a new SSH session.
 */
class ClientSSHShellFactory implements ShellFactory {
    /**
     * Creates a new SSH session.
     * @param channel ChannelSession channel.
     * @return A new instance of <i>MySSHClientHandler</i>.
     */
    @Override
    public Command createShell(ChannelSession channel) {
        return new ClientSSHHandler();
    }
}
