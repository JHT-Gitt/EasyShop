package org.yearup.data.mysql;

import org.springframework.stereotype.Repository;
import org.yearup.data.ShoppingCartDao;
import org.yearup.models.Product;
import org.yearup.models.ShoppingCart;
import org.yearup.models.ShoppingCartItem;

import javax.sql.DataSource;
import java.sql.*;
import java.util.HashMap;

@Repository
public class MySqlShoppingCartDao implements ShoppingCartDao
{
    private final DataSource dataSource;

    public MySqlShoppingCartDao(DataSource dataSource)
    {
        this.dataSource = dataSource;
    }
    @Override
    public ShoppingCart getByUserId(int userId)
    {
        ShoppingCart cart = new ShoppingCart();
        cart.setItems(new HashMap<>());

        String sql = """
        SELECT
            sc.product_id,
            sc.quantity,
            p.product_id,
            p.name,
            p.price,
            p.category_id,
            p.description,
            p.color,
            p.stock,
            p.image_url,
            p.featured
        FROM
            shopping_cart sc
        JOIN
            products p ON sc.product_id = p.product_id
        WHERE
            sc.user_id = ?
        """;

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql))
        {
            statement.setInt(1, userId);
            ResultSet rs = statement.executeQuery();

            while (rs.next())
            {
                Product product = new Product();
                product.setProductId(rs.getInt("product_id"));
                product.setName(rs.getString("name"));
                product.setPrice(rs.getBigDecimal("price"));
                product.setCategoryId(rs.getInt("category_id"));
                product.setDescription(rs.getString("description"));
                product.setColor(rs.getString("color"));
                product.setStock(rs.getInt("stock"));
                product.setImageUrl(rs.getString("image_url"));
                product.setFeatured(rs.getBoolean("featured"));

                ShoppingCartItem item = new ShoppingCartItem();
                item.setProduct(product);
                item.setQuantity(rs.getInt("quantity"));
               // item.setDiscountPercent(rs.getBigDecimal("discount_percent"));  // optional, default to 0 if missing

                cart.getItems().put(product.getProductId(), item);
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            throw new RuntimeException("Failed to load shopping cart items.", e);
        }

        return cart;
    }

//    public ShoppingCart getByUserId(int userId)
//    {
//        ShoppingCart cart = new ShoppingCart(userId);
//        String sql = "SELECT product_id, quantity FROM shopping_cart WHERE user_id = ?";
//
//        try (Connection conn = dataSource.getConnection();
//             PreparedStatement stmt = conn.prepareStatement(sql))
//        {
//            stmt.setInt(1, userId);
//            ResultSet rs = stmt.executeQuery();
//
//            while (rs.next())
//            {
//                int productId = rs.getInt("product_id");
//                int quantity = rs.getInt("quantity");
//
//                Product product = productDao.getById(productId); // 💡 ensure this is not null and price is set
//                ShoppingCartItem item = new ShoppingCartItem(product, quantity);
//                cart.addItem(item);
//            }
//
//            return cart;
//        }
//        catch (SQLException e)
//        {
//            throw new RuntimeException("Failed to load shopping cart", e);
//        }
//    }


    //    @Override
//    public ShoppingCart getByUserId(int userId)
//    {
//        ShoppingCart cart = new ShoppingCart(userId);
//        String sql = "SELECT product_id, quantity FROM shopping_cart WHERE user_id = ?";
//
//        try (Connection connection = dataSource.getConnection();
//             PreparedStatement statement = connection.prepareStatement(sql))
//        {
//            statement.setInt(1, userId);
//            ResultSet rs = statement.executeQuery();
//
//            while (rs.next())
//            {
//                int productId = rs.getInt("product_id");
//                int quantity = rs.getInt("quantity");
//                cart.addItem(productId, quantity);
//            }
//        }
//        catch (SQLException e)
//        {
//            e.printStackTrace();
//            throw new RuntimeException("Failed to retrieve shopping cart.");
//        }
//
//        return cart;
//    }
    @Override
    public void addToCart(int userId, int productId, int quantity) {
        String sql = "MERGE INTO shopping_cart AS target " +
                "USING (SELECT ? AS user_id, ? AS product_id, ? AS quantity) AS source " +
                "ON target.user_id = source.user_id AND target.product_id = source.product_id " +
                "WHEN MATCHED THEN " +
                "    UPDATE SET quantity = target.quantity + source.quantity " +
                "WHEN NOT MATCHED THEN " +
                "    INSERT (user_id, product_id, quantity) VALUES (source.user_id, source.product_id, source.quantity);";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);
            statement.setInt(2, productId);
            statement.setInt(3, quantity);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to add product to cart.");
        }
    }


//    @Override
//    public void addToCart(int userId, int productId, int quantity)
//    {
//        String sql = "INSERT INTO shopping_cart (user_id, product_id, quantity) " +
//                "VALUES (?, ?, ?) " +
//                "ON DUPLICATE KEY UPDATE quantity = quantity + VALUES(quantity)";
//
//        try (Connection connection = dataSource.getConnection();
//             PreparedStatement statement = connection.prepareStatement(sql))
//        {
//            statement.setInt(1, userId);
//            statement.setInt(2, productId);
//            statement.setInt(3, quantity);
//            statement.executeUpdate();
//        }
//        catch (SQLException e)
//        {
//            e.printStackTrace();
//            throw new RuntimeException("Failed to add product to cart.");
//        }
//    }


    @Override
    public void updateCartItem(int userId, int productId, int quantity)
    {
        String sql = "UPDATE shopping_cart SET quantity = ? WHERE user_id = ? AND product_id = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql))
        {
            statement.setInt(1, quantity);
            statement.setInt(2, userId);
            statement.setInt(3, productId);
            statement.executeUpdate();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            throw new RuntimeException("Failed to update cart item.");
        }
    }

    @Override
    public void clearCart(int userId)
    {
        String sql = "DELETE FROM shopping_cart WHERE user_id = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql))
        {
            statement.setInt(1, userId);
            statement.executeUpdate();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            throw new RuntimeException("Failed to clear cart.");
        }
    }
}