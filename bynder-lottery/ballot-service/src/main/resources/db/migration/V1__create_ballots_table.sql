CREATE TABLE ballots (
    id BIGSERIAL PRIMARY KEY,
    participant_id BIGINT NOT NULL,
    lottery_id BIGINT NOT NULL,
    lottery_type VARCHAR(50) NOT NULL,
    lottery_name VARCHAR(100) NOT NULL,
    numbers INTEGER[] NOT NULL,
    created_at TIMESTAMP NOT NULL,
    prize VARCHAR(20)
);

CREATE INDEX idx_ballots_participant_created_at ON ballots(participant_id, created_at DESC);
CREATE INDEX idx_ballots_lottery_id ON ballots(lottery_id);
