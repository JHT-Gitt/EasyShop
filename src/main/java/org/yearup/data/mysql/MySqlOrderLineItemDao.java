package org.yearup.data.mysql;

import org.springframework.stereotype.Component;
import org.yearup.data.OrderLineItemDao;
import org.yearup.models.OrderLineItem;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Component
public class MySqlOrderLineItemDao extends MySqlDaoBase implements OrderLineItemDao
{
    public MySqlOrderLineItemDao(DataSource dataSource)
    {
        super(dataSource);
    }

    @Override
    public void addLineItem(OrderLineItem lineItem)
    {
        String sql = """
            INSERT INTO capstone.order_line_items (order_id, product_id, sales_price, quantity, discount)
            VALUES (?, ?, ?, ?, ?)
            """;

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql))
        {
            statement.setInt(1, lineItem.getOrderId());
            statement.setInt(2, lineItem.getProductId());
            statement.setBigDecimal(3, lineItem.getSalesPrice());
            statement.setInt(4, lineItem.getQuantity());
            statement.setBigDecimal(5, lineItem.getDiscount());

            statement.executeUpdate();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            throw new RuntimeException("Failed to insert order line item.");
        }
    }
}
