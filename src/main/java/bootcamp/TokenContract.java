package bootcamp;

import net.corda.core.contracts.Command;
import net.corda.core.contracts.CommandData;
import net.corda.core.contracts.Contract;
import net.corda.core.contracts.ContractState;
import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.Party;
import net.corda.core.transactions.LedgerTransaction;
import static net.corda.core.contracts.ContractsDSL.requireSingleCommand;    
import static net.corda.core.contracts.ContractsDSL.requireThat;

import java.security.PublicKey;
import java.util.List;

/* Our contract, governing how our state will evolve over time.
 * See src/main/java/examples/ArtContract.java for an example. */
public class TokenContract implements Contract {
    public static String ID = "bootcamp.TokenContract";


    public void verify(LedgerTransaction tx) throws IllegalArgumentException {
        // Shape constraints
        Command command = tx.getCommand(0);
        CommandData commandType = command.getValue();

        if(!(commandType instanceof Commands))
            throw new IllegalArgumentException("Amount must be more than zero");

        if(tx.getInputStates().size() != 0)
            throw new IllegalArgumentException("No inputs expected");
        if(tx.getOutputStates().size() != 1)
            throw new IllegalArgumentException("Only 1 output expected");
        if(tx.getCommands().size() != 1 )
            throw new IllegalArgumentException("Only 1 command expected");

        // Content constraints
        ContractState outputState = tx.getOutput(0);
        if(!(outputState instanceof TokenState))
            throw new IllegalArgumentException("Output must be Token state");

        TokenState outputTokenState = (TokenState) outputState;
        if(outputTokenState.getAmount() <= 0)
            throw new IllegalArgumentException("Amount must be more than zero");

        // Required signer constraints
        List<PublicKey> requiredSigners = command.getSigners();
        Party issuer = outputTokenState.getIssuer();

        if(!(requiredSigners.contains(issuer.getOwningKey())))
            throw new IllegalArgumentException("Issuer must be required signer");
    }

    public interface Commands extends CommandData {
        class Issue implements Commands { }
    }
}
