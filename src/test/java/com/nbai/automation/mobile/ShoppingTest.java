package com.nbai.automation.mobile;

import com.nbai.automation.mobile.config.MobileCredential;
import com.nbai.automation.mobile.screen.CartScreen;
import com.nbai.automation.mobile.screen.CheckoutAddressScreen;
import com.nbai.automation.mobile.screen.ProductScreen;
import org.testng.Assert;
import org.testng.annotations.Test;

public final class ShoppingTest extends BaseMobileTest {

    @Test(groups = "mobile", description = "Open a catalog product and verify its details")
    public void catalogProductShowsExpectedDetails() {
        ProductScreen product = mobile.catalog().openBackpack();

        Assert.assertTrue(product.isLoaded(), "Backpack detail screen was not displayed");
        Assert.assertEquals(product.productName(), "Sauce Labs Backpack");
        Assert.assertEquals(product.productPrice(), "$29.99");
    }

    @Test(groups = "mobile", description = "Add a product and verify the cart")
    public void addedProductAppearsInCart() {
        ProductScreen product = mobile.catalog().openBackpack();
        Assert.assertTrue(product.isLoaded(), "Backpack detail screen was not displayed");

        product.addToCart();
        Assert.assertEquals(product.cartCount(), "1", "Cart badge did not show one item");
        CartScreen cart = product.openCart();

        Assert.assertTrue(cart.isLoaded(), "Cart screen was not displayed");
        Assert.assertEquals(cart.backpackName(), "Sauce Labs Backpack");
        Assert.assertEquals(cart.quantity(), "1", "Cart quantity did not show one item");
        Assert.assertEquals(
                cart.displayedItemAndTotalPrices(),
                java.util.List.of("$29.99", "$29.99"),
                "Cart item price and total were not both $29.99");
    }

    @Test(groups = "mobile", description = "Proceed from cart to checkout address")
    public void signedInUserCanStartCheckout() {
        MobileCredential credential = mobile.credentials().valid();
        var catalog = mobile.catalog().openMenu()
                .openLogin()
                .loginSuccessfully(credential.username(), credential.password());
        Assert.assertTrue(catalog.isLoaded(), "Valid login did not return to the product catalog");

        CheckoutAddressScreen checkout = catalog.openBackpack()
                .addToCart()
                .openCart()
                .proceedToCheckout();

        Assert.assertTrue(checkout.isLoaded(), "Checkout address form was not displayed");
        Assert.assertTrue(checkout.isFullNameRequiredInputDisplayed(), "Full Name required input was absent");
        Assert.assertTrue(
                checkout.isAddressLineOneRequiredInputDisplayed(),
                "Address Line 1 required input was absent");
        Assert.assertTrue(checkout.isCityRequiredInputDisplayed(), "City required input was absent");
        Assert.assertTrue(checkout.isZipCodeRequiredInputDisplayed(), "Zip Code required input was absent");
        Assert.assertTrue(checkout.isCountryRequiredInputDisplayed(), "Country required input was absent");
        Assert.assertTrue(checkout.isToPaymentDisplayed(), "To Payment button was absent");
    }
}
