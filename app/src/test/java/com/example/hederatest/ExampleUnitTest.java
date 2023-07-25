package com.example.hederatest;

import android.util.Log;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.threeten.bp.Duration;


import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.Client;
import com.hedera.hashgraph.sdk.Hbar;
import com.hedera.hashgraph.sdk.Mnemonic;
import com.hedera.hashgraph.sdk.PrivateKey;
import com.hedera.hashgraph.sdk.TransactionId;
import com.hedera.hashgraph.sdk.TransactionReceipt;
import com.hedera.hashgraph.sdk.TransactionResponse;
import com.hedera.hashgraph.sdk.TransferTransaction;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.logging.Logger;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {


    @Test
    public void testHederaTransfer() throws Exception {
        System.out.println(getCurrentTime() + "  start test.");

        String mnemonic = "comfort assume rookie fault zoo fatigue river marriage match idle water solution";
        PrivateKey ownerPrivateKey = PrivateKey.fromMnemonic(Mnemonic.fromString(mnemonic)).derive(0);

        AccountId ownerAccount = AccountId.fromString("0.0.15538452"); // account which belongs to the mnemonic above.
        AccountId toAccount = AccountId.fromString("0.0.6295"); // account to receive HBAR

        Client client = Client.forTestnet();
        client.setOperator(ownerAccount, ownerPrivateKey);

        TransferTransaction transaction = new TransferTransaction()
                .addHbarTransfer(ownerAccount, new Hbar(-1))
                .addHbarTransfer(toAccount, new Hbar(1))
                .setNodeAccountIds(Collections.singletonList(AccountId.fromString("0.0.3")))
                .setTransactionId(TransactionId.generate(ownerAccount))
                .freezeWith(client);

        String txId = transaction.getTransactionId().toString();
        System.out.println(getCurrentTime() + "  executing transaction txId=" + txId + "...");

        try {
            TransactionResponse transactionResponse = transaction.execute(client);

            System.out.println(getCurrentTime() + "  getting receipt for " + txId + "...");

            TransactionReceipt receipt = transactionResponse.getReceipt(client, Duration.ofSeconds(2)); // (PROBLEM HERE) It should wait for exact 2 seconds, not 10 seconds.
            System.out.println(getCurrentTime() + "  Completed. receipt status=" + receipt.status);
        } catch (java.util.concurrent.TimeoutException e) { // TimeoutException Occured in 10 seconds although I set timeout duration as 2 seoncds.
            System.out.println(getCurrentTime() + "  TimeoutException=" + e.getStackTrace().toString());
        }
        System.out.println(getCurrentTime() + "  Completed.");
    }

    private static String getCurrentTime() {
        String currentTs = String.valueOf(Calendar.getInstance().getTimeInMillis());
        Timestamp ts = new Timestamp(Long.parseLong(currentTs));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(new Date(ts.getTime()));
    }
}