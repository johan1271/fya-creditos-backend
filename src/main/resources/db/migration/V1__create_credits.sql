CREATE TABLE credits (
    id BIGSERIAL PRIMARY KEY,
    customer_name VARCHAR(150) NOT NULL,
    id_number VARCHAR(20) NOT NULL,
    credit_amount NUMERIC(15,2) NOT NULL,
    interest_rate NUMERIC(5,2) NOT NULL,
    term_months INT NOT NULL,
    sales_agent VARCHAR(150) NOT NULL,
    registered_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX idx_credits_customer_name ON credits (customer_name);
CREATE INDEX idx_credits_id_number ON credits (id_number);
CREATE INDEX idx_credits_sales_agent ON credits (sales_agent);
