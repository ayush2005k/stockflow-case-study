import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

@GetMapping("/api/companies/{companyId}/alerts/low-stock")
public ResponseEntity<?> getLowStockAlerts(@PathVariable Long companyId) {

    try {
        // I'll treat "recent" as last 30 days
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(30);

        // Step 1: Get average daily sales per product per warehouse
        List<Object[]> salesData = salesRepository.getAverageDailySales(cutoffDate);

        // Converting this into a map makes lookup easier later
        Map<String, Double> avgSalesMap = new HashMap<>();
        for (Object[] row : salesData) {
            Long productId = (Long) row[0];
            Long warehouseId = (Long) row[1];
            Double avgSales = (Double) row[2];

            String key = productId + "_" + warehouseId;
            avgSalesMap.put(key, avgSales);
        }

        // Step 2: Fetch all inventory entries where stock is below threshold
        List<Inventory> lowStockItems = inventoryRepository.findLowStockByCompany(companyId);

        List<Map<String, Object>> alerts = new ArrayList<>();

        for (Inventory inv : lowStockItems) {

            Long productId = inv.getProduct().getId();
            Long warehouseId = inv.getWarehouse().getId();

            String key = productId + "_" + warehouseId;
            Double avgSales = avgSalesMap.get(key);

            // If there's no recent sales data, I skip it
            if (avgSales == null || avgSales == 0) {
                continue;
            }

            int currentStock = inv.getQuantity();
            int threshold = inv.getProduct().getThreshold();

            // Simple estimate of how many days stock will last
            int daysUntilStockout = (int) (currentStock / avgSales);

            // Getting supplier (taking first one for simplicity)
            Supplier supplier = inv.getProduct()
                                   .getSuppliers()
                                   .stream()
                                   .findFirst()
                                   .orElse(null);

            Map<String, Object> alert = new HashMap<>();
            alert.put("product_id", productId);
            alert.put("product_name", inv.getProduct().getName());
            alert.put("sku", inv.getProduct().getSku());
            alert.put("warehouse_id", warehouseId);
            alert.put("warehouse_name", inv.getWarehouse().getName());
            alert.put("current_stock", currentStock);
            alert.put("threshold", threshold);
            alert.put("days_until_stockout", daysUntilStockout);

            if (supplier != null) {
                Map<String, Object> supplierData = new HashMap<>();
                supplierData.put("id", supplier.getId());
                supplierData.put("name", supplier.getName());
                supplierData.put("contact_email", supplier.getContactEmail());
                alert.put("supplier", supplierData);
            }

            alerts.add(alert);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("alerts", alerts);
        response.put("total_alerts", alerts.size());

        return ResponseEntity.ok(response);

    } catch (Exception e) {
        // In a real system, I would log this properly
        return ResponseEntity.status(500)
                .body(Map.of("error", "Something went wrong"));
    }
}

/* Approach
First, I calculate average daily sales using recent data
Then, I fetch products where stock is below the threshold
I combine both using a map for quick lookup
For each product, I estimate how long the stock will last
Finally, I include supplier info so the result is useful for reordering
*/

/*Edge Cases Considered
If there are no recent sales, I skip the product (since prediction wouldn’t be meaningful)
If avgSales = 0, I avoid division by zero
If a product has no supplier, I handle it safely without breaking the response
If the company has no low-stock items, the API returns an empty list
Handles multiple warehouses by checking inventory per warehouse
 */