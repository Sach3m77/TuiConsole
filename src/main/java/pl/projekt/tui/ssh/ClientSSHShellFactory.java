package pl.projekt.tui.ssh;

import org.apache.sshd.server.channel.ChannelSession;
import org.apache.sshd.server.command.Command;
import org.apache.sshd.server.shell.ShellFactory;

/**
 * ShellFactory implementation for creating SSH shells.
 * This factory is responsible for creating instances of ClientSSHHandler as SSH shells.
 */
class ClientSSHShellFactory implements ShellFactory {

    /**
     * Creates a new SSH shell instance using ClientSSHHandler.
     *
     * @param channel The SSH channel session associated with the shell
     * @return A new Command instance representing the SSH shell
     */
    @Override
    public Command createShell(ChannelSession channel) {
        return new ClientHandler();
    }
}
