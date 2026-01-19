const API_BASE = 'http://localhost:8080/api';

// Simple in-memory auth state (resets on page refresh/close)
function isLoggedIn() {
    return sessionStorage.getItem('user') !== null;
}

function getUser() {
    const user = sessionStorage.getItem('user');
    return user ? JSON.parse(user) : null;
}

function setUser(user) {
    sessionStorage.setItem('user', JSON.stringify(user));
}

function logout() {
    sessionStorage.removeItem('user');
    window.location.href = '../index.html';
}

function requireAuth() {
    if (!isLoggedIn()) {
        window.location.href = 'auth-required.html';
        return false;
    }
    return true;
}

function showTopButtons(options = {}) {
    if (!isLoggedIn()) return;
    
    const { hideCheckBet = false, hideHome = false } = options;
    
    const container = document.createElement('div');
    container.className = 'top-buttons';
    
    if (!hideHome) {
        const homeBtn = document.createElement('button');
        homeBtn.className = 'btn btn-primary';
        homeBtn.textContent = 'Home';
        homeBtn.onclick = () => window.location.href = 'home.html';
        container.appendChild(homeBtn);
    }
    
    if (!hideCheckBet) {
        const checkBtn = document.createElement('button');
        checkBtn.className = 'btn btn-success';
        checkBtn.textContent = 'Check Bet';
        checkBtn.onclick = () => window.location.href = 'check.html';
        container.appendChild(checkBtn);
    }
    
    const logoutBtn = document.createElement('button');
    logoutBtn.className = 'btn btn-danger';
    logoutBtn.textContent = 'Logout';
    logoutBtn.onclick = logout;
    container.appendChild(logoutBtn);
    
    document.body.appendChild(container);
}

function showGuestButtons() {
    const container = document.createElement('div');
    container.className = 'top-buttons';
    
    const homeBtn = document.createElement('button');
    homeBtn.className = 'btn btn-secondary';
    homeBtn.textContent = 'Home';
    homeBtn.onclick = () => window.location.href = '../index.html';
    container.appendChild(homeBtn);
    
    const registerBtn = document.createElement('button');
    registerBtn.className = 'btn btn-primary';
    registerBtn.textContent = 'Register';
    registerBtn.onclick = () => window.location.href = 'register.html';
    container.appendChild(registerBtn);
    
    const loginBtn = document.createElement('button');
    loginBtn.className = 'btn btn-success';
    loginBtn.textContent = 'Login';
    loginBtn.onclick = () => window.location.href = 'login.html';
    container.appendChild(loginBtn);
    
    document.body.appendChild(container);
}

function showLogoutButton() {
    showTopButtons();
}

const COLORS = [
    '#e74c3c', '#9b59b6', '#3498db', '#1abc9c', 
    '#f39c12', '#e67e22', '#2ecc71', '#34495e'
];

function getRandomColor(index) {
    return COLORS[index % COLORS.length];
}

async function getErrorMessage(response, fallback) {
    try {
        const data = await response.json();
        return data && data.message ? data.message : fallback;
    } catch (error) {
        return fallback;
    }
}

function formatDate(dateStr) {
    if (!dateStr) return '';
    const date = new Date(dateStr.split('T')[0]);
    const day = date.getDate();
    const month = date.toLocaleString('en-GB', { month: 'long' });
    const year = date.getFullYear();
    return `${day} ${month} ${year}`;
}

async function loadLotteries(containerId, fromRoot = false) {
    const container = document.getElementById(containerId);
    container.innerHTML = '<p>Loading lotteries...</p>';

    try {
        const response = await fetch(`${API_BASE}/lotteries/open`);
        if (!response.ok) {
            const message = await getErrorMessage(response, 'Failed to load lotteries.');
            container.innerHTML = `<p class="message error">${message}</p>`;
            return;
        }
        const lotteries = await response.json();

        container.innerHTML = '';
        lotteries.forEach((lottery, index) => {
            const card = document.createElement('div');
            card.className = 'lottery-card';
            card.style.background = getRandomColor(index);
            card.innerHTML = `
                <h3>${lottery.name}</h3>
                <p>Ends: ${formatDate(lottery.endDate)}</p>
            `;
            card.onclick = () => {
                const basePath = fromRoot ? 'frontend/' : '';
                window.location.href = `${basePath}lottery.html?id=${lottery.id}&type=${lottery.lotteryType}&name=${encodeURIComponent(lottery.name)}&endDate=${lottery.endDate}`;
            };
            container.appendChild(card);
        });

        if (lotteries.length === 0) {
            container.innerHTML = '<p class="message info">No open lotteries at the moment.</p>';
        }
    } catch (error) {
        console.error('Error loading lotteries:', error);
        container.innerHTML = '<p class="message error">Error connecting to server. Make sure the backend is running.</p>';
    }
}

async function register(name, email, password) {
    try {
        const response = await fetch(`${API_BASE}/participants/register`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ name, email, password })
        });
        if (!response.ok) {
            const message = await getErrorMessage(response, 'Registration failed');
            return { success: false, message };
        }
        const user = await response.json();
        setUser(user);
        return { success: true };
    } catch (error) {
        console.error('Registration error:', error);
        return { success: false, message: 'Error connecting to server' };
    }
}

async function login(email, password) {
    try {
        const response = await fetch(`${API_BASE}/participants/login`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email, password })
        });
        if (!response.ok) {
            const message = await getErrorMessage(response, 'Login failed');
            return { success: false, message };
        }
        const user = await response.json();
        setUser(user);
        return { success: true };
    } catch (error) {
        console.error('Login error:', error);
        return { success: false, message: 'Error connecting to server' };
    }
}

async function placeBet(lotteryId, lotteryType, lotteryName, numbers) {
    const user = getUser();
    if (!user) {
        return { success: false, message: 'Not logged in' };
    }

    try {
        const response = await fetch(`${API_BASE}/ballots`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                participantId: user.participantId,
                lotteryId: lotteryId,
                lotteryType: lotteryType,
                lotteryName: lotteryName,
                numbers: numbers
            })
        });
        if (!response.ok) {
            const message = await getErrorMessage(response, 'Failed to place bet');
            return { success: false, message };
        }
        await response.json().catch(() => null);
        return { success: true, message: 'Bet placed successfully!' };
    } catch (error) {
        console.error('Place bet error:', error);
        return { success: false, message: 'Error connecting to server' };
    }
}

async function getMyBets() {
    const user = getUser();
    if (!user) {
        return { success: false, data: [] };
    }

    try {
        const response = await fetch(`${API_BASE}/ballots/${user.participantId}`);
        if (!response.ok) {
            return { success: false, data: [] };
        }
        const bets = await response.json();
        return { success: true, data: Array.isArray(bets) ? bets : [] };
    } catch (error) {
        console.error('Get bets error:', error);
        return { success: false, data: [] };
    }
}

async function checkBet(lotteryType, endDate, numbers) {
    try {
        const response = await fetch(`${API_BASE}/lotteries/check-ballot`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                lotteryType: lotteryType,
                endDate: endDate,
                ballotNumbers: numbers
            })
        });
        if (!response.ok) {
            const message = await getErrorMessage(response, 'Failed to check bet');
            return { success: false, message };
        }
        const result = await response.json();
        return { success: true, prize: result.prize };
    } catch (error) {
        console.error('Check bet error:', error);
        return { success: false, message: 'Error connecting to server' };
    }
}
