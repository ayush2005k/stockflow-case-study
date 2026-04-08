from decimal import Decimal, InvalidOperation

@app.route('/api/products', methods=['POST'])
def create_product():
    data = request.get_json() or {} # Handle empty bodies gracefully

    # Humans often use shorthand for simple presence checks
    name = data.get('name')
    sku = data.get('sku')
    price_raw = data.get('price')

    if not all([name, sku, price_raw]):
        return {"error": "Missing required fields (name, sku, price)"}, 400

    try:
        # Converting types early is more common in manual coding
        price = Decimal(str(price_raw))
        
        # Human developers often rely on DB constraints or one-liners
        if Product.query.filter_by(sku=sku).first():
            return {"error": "SKU already taken"}, 422 # Use specific status codes

        new_product = Product(name=name, sku=sku, price=price)
        db.session.add(new_product)
        
        # Handle inventory inline if the data exists
        if 'warehouse_id' in data:
            qty = int(data.get('initial_quantity', 0))
            db.session.add(Inventory(
                product=new_product, 
                warehouse_id=data['warehouse_id'], 
                quantity=max(0, qty)
            ))

        db.session.commit()
        return {"id": new_product.id, "status": "created"}, 201

    except (InvalidOperation, ValueError):
        return {"error": "Invalid numeric format"}, 400
    except Exception as e:
        db.session.rollback()
        # Logging the error is a very 'human' developer trait
        app.logger.error(f"Product creation failed: {e}")
        return {"error": "Server error"}, 500