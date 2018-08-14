package tamirmo.shopper.Receipt;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import tamirmo.shopper.Database.Class.CartItem;
import tamirmo.shopper.Database.Class.Discount;
import tamirmo.shopper.Database.Class.Product;
import tamirmo.shopper.Database.Class.Sale;
import tamirmo.shopper.R;

public class ReceiptListAdapter extends ArrayAdapter<CartItem> {

    // Class attributes
    private Context context;

    private List<CartItem> paidCart;
    private HashMap<String, Product> productMap;
    private HashMap<String, Discount> discountMap;
    private HashMap<String, Sale> saleMap;

    ReceiptListAdapter(Context context, List<CartItem> cart, List<Product> products, List<Discount> discounts, List<Sale> sales){
        super(context, R.layout.receipt_layout, new CartItem[0]);
        this.context = context;

        paidCart = getPaidCart(cart);
        productMap = getProductMap(products);
        discountMap = getDiscountMap(discounts);
        saleMap = getSalesMap(sales);

        sales.clear();
        cart.clear();
    }

    // Returns a cart with only paid items
    public List<CartItem> getPaidCart(List<CartItem> cart){
        List<CartItem> filteredCart = new ArrayList<CartItem>();

        for(CartItem item : cart){
            if(item.getIsPicked()){
                filteredCart.add(item);
            }
        }

        return filteredCart;
    }

    // Returns a HashMap from a List of products
    public HashMap<String, Product> getProductMap(List<Product> productList){
        HashMap<String, Product> productMap = new HashMap<String, Product>();

        for(Product product : productList){
            productMap.put(product.getProductId(), product);
        }

        return productMap;
    }

    // Returns a HashMap from a List of discounts
    public HashMap<String, Discount> getDiscountMap(List<Discount> discountList) {
        HashMap<String, Discount> discountMap = new HashMap<String, Discount>();

        for(Discount discount : discountList){
            discountMap.put(discount.getProductId(), discount);
        }

        return discountMap;
    }

    // Returns a HashMap from a List of sales
    public HashMap<String, Sale> getSalesMap(List<Sale> saleList) {
        HashMap<String, Sale> saleMap = new HashMap<String, Sale>();

        for(Sale sale : saleList){
            saleMap.put(sale.getProductID(), sale);
        }

        return saleMap;
    }

    @Override
    public int getCount(){
        return paidCart.size();
    }

    @Nullable
    @Override
    public CartItem getItem(int position){
        return paidCart.get(position);
    }

    @Nullable
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent){
        CartItem cartItem = getItem(position);
        Product product = productMap.get(cartItem.getProductID());
        Discount discount = discountMap.get(cartItem.getProductID());

        // Creates a view holder for current product
        ProductAdapterUtilities.ProductViewObjects productViewObjects = ProductAdapterUtilities.createViewHolder(convertView, parent, context);
        ProductAdapterUtilities.ProductItemViewHolder viewHolder = productViewObjects.productItemViewHolder;
        ProductAdapterUtilities.setProductData(product, viewHolder, context);

        if(discount != null)
            viewHolder.price.setText(String.format("$%s", discount.getDiscountedPrice()));
        else
            viewHolder.price.setText(String.format("$%s", product.getPricePerUnit()));
        viewHolder.amount.setText(String.valueOf(cartItem.getAmount()));
        viewHolder.amount.setVisibility(View.VISIBLE);
        viewHolder.pickedImage.setVisibility(View.VISIBLE);

        // Return the completed view to render on screen
        return productViewObjects.convertView;
    }

    // Calculates total sum of list
    public double calculateTotalSum() {
        double sum = 0;

        for ( CartItem item : paidCart){
            sum += calculateSum(item);
        }

        return sum;
    }

    // Calculates total sum of a single cart item
    public double calculateSum(CartItem item){
        double sum = 0;

        String productID = item.getProductID();
        int amount = item.getAmount();
        Sale sale = saleMap.get(productID);
        Discount discount = discountMap.get(productID);
        Product product = productMap.get(productID);
        int payAmount = 0;

        // Calculates how many products need to be paid
        if(sale != null) {
            int salePayAmount = sale.getPayAmount();
            int saleFreeAmount = sale.getFreeAmount();
            int completedSales = amount / (salePayAmount + saleFreeAmount);
            amount -= completedSales;
            payAmount = completedSales;
            while (amount > 0){
                for (int i = salePayAmount; i > 0 && amount > 0; i--) {
                    payAmount++;
                    amount--;
                }
                for (int i = saleFreeAmount; i > 0 && amount > 0; i--) {
                    amount--;
                }
            }
            amount = payAmount;
        }

        // Updates total price
        if(discount != null) {
            sum += amount * Double.parseDouble(discount.getDiscountedPrice());
        }
        else{
            sum += amount * Double.parseDouble(product.getPricePerUnit());
        }

        return sum;
    }

    // Returns a receipt
    public String getReceipt(){
        StringBuilder reciptBuilder = new StringBuilder();
        String receipt;
        double sum = 0, itemPrice;

        for(CartItem item : paidCart){
            Product product = productMap.get(item.getProductID());
            if(product != null){
                reciptBuilder.append(product.getName()+" "+ product.getAmountPerUnit()+ " "+product.getUnitType().name());
                reciptBuilder.append(" x "+item.getAmount());
                itemPrice = calculateSum(item);
                sum += itemPrice;
                reciptBuilder.append(String.format(" : $%.2f", itemPrice));
                reciptBuilder.append(" "+context.getString(R.string.receipt_delimiter)+" ");
            }
        }
        reciptBuilder.append(String.format("Total : $%.2f", sum));
        receipt = reciptBuilder.toString();

        return receipt;
    }















}
