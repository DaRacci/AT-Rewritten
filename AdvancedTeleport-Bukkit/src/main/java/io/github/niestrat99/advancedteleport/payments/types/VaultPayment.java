package io.github.niestrat99.advancedteleport.payments.types;

import io.github.niestrat99.advancedteleport.CoreClass;
import io.github.niestrat99.advancedteleport.config.CustomMessages;
import io.github.niestrat99.advancedteleport.payments.Payment;
import org.bukkit.entity.Player;

public class VaultPayment extends Payment {

    private double price;

    public VaultPayment(double price) {
        this.price = price;
    }

    @Override
    public double getPaymentAmount() {
        return price;
    }

    @Override
    public void setPaymentAmount(double amount) {
        price = amount;
    }

    @Override
    public boolean canPay(Player player) {
        boolean result = super.canPay(player);
        if (!result) {
            CustomMessages.sendMessage(player, "Error.notEnoughMoney", "{amount}", String.valueOf(price));
        }
        return result;
    }

    @Override
    public double getPlayerAmount(Player player) {
        return CoreClass.getVault().getBalance(player);
    }

    @Override
    public String getMessagePath() {
        return "Info.paymentVault";
    }

    @Override
    public void setPlayerAmount(Player player) {
        CoreClass.getVault().withdrawPlayer(player, price);
        CustomMessages.sendMessage(player, "Info.paymentVault", "{amount}", String.valueOf(price), "{balance}", String.valueOf(getPlayerAmount(player)));
    }
}
