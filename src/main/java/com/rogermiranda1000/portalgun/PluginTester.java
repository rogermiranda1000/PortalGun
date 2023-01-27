package com.rogermiranda1000.portalgun;

import com.rogermiranda1000.watchwolf.entities.*;
import com.rogermiranda1000.watchwolf.tester.Tester;
import com.rogermiranda1000.watchwolf.tester.TesterConnector;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class PluginTester {
    public static void main(String[] args) {
        try {
            Socket serversManagerSocket = new Socket("127.0.0.1", 8000); // ServersManager socket
            Plugin []plugins = new Plugin[]{
                    new UsualPlugin("PortalGun")
            };

            String []versions = new String[]{"1.19"/*, "1.8"*/};
            for (String vestion : versions) {
                System.out.println("Starting test for Spigot " + vestion);
                Tester tester = new com.rogermiranda1000.watchwolf.tester.Tester(serversManagerSocket, ServerType.Spigot, vestion, plugins, new Map[]{}, new ConfigFile[]{})
                        .setOnServerError(Tester.DEFAULT_ERROR_PRINT);

                tester.setOnServerStart(() -> {
                    try {
                        TesterConnector connector = tester.getConnector();
                        connector.whitelistPlayer("MinecraftGamer_Z");
                        connector.opPlayer("rogermiranda1000");

                        new Scanner(System.in).nextLine(); // wait till enter (manual testing)
                        tester.close();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                });

                tester.run(); // all prepared, start the server
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
