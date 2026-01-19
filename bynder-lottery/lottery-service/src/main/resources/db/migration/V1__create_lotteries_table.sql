CREATE TABLE lotteries (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    lottery_type VARCHAR(50) NOT NULL,
    status VARCHAR(20) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    winning_numbers INTEGER[]
);
