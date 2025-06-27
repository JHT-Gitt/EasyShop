package org.yearup.data;

import org.yearup.models.OrderLineItem;

public interface OrderLineItemDao
{
    void addLineItem(OrderLineItem lineItem);
}
