package org.yearup.data.mysql;

import org.springframework.stereotype.Component;
import org.yearup.data.OrderDao;
import org.yearup.models.Order;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
public class MySqlOrderDao extends MySqlDaoBase implements OrderDao
{
    public MySqlOrderDao(DataSource dataSource)
    {
        super(dataSource);
    }

    @Override
    public int createOrder(Order order)
    {
        String sql = """
            INSERT INTO capstone.orders (user_id, date, address, city, state, zip, shipping_amount)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            """;

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS))
        {
            statement.setInt(1, order.getUserId());
            statement.setTimestamp(2, order.getDateTime() != null ? Timestamp.valueOf(order.getDateTime()) : new Timestamp(System.currentTimeMillis()));
            statement.setString(3, order.getAddress());
            statement.setString(4, order.getCity());
            statement.setString(5, order.getState());
            statement.setString(6, order.getZip());
            statement.setBigDecimal(7, order.getShippingAmount());

            statement.executeUpdate();

            ResultSet keys = statement.getGeneratedKeys();
            if (keys.next())
            {
                return keys.getInt(1);
            }
            else
            {
                throw new SQLException("Order creation failed, no ID returned.");
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            throw new RuntimeException("Failed to create order.");
        }
    }
}
