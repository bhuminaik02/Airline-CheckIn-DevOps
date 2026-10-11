const lookupForm = document.querySelector('#lookup-form');
const seatForm = document.querySelector('#seat-form');
const checkInForm = document.querySelector('#check-in-form');
const message = document.querySelector('#message');
const bookingPanel = document.querySelector('#booking-panel');
const seatChoice = document.querySelector('#seat-choice');

let currentBooking = null;

for (let row = 1; row <= 39; row += 1) {
    for (const letter of ['A', 'B', 'C', 'D', 'E', 'F']) {
        const option = document.createElement('option');
        option.value = `${row}${letter}`;
        option.textContent = `${row}${letter}`;
        seatChoice.append(option);
    }
}

function showMessage(text, isSuccess = false) {
    message.textContent = text;
    message.classList.toggle('success', isSuccess);
}

async function apiRequest(url, options = {}) {
    const response = await fetch(url, {
        ...options,
        headers: { 'Content-Type': 'application/json', ...options.headers },
    });
    const body = await response.json().catch(() => ({}));
    if (!response.ok) {
        throw new Error(body.message || 'The request could not be completed.');
    }
    return body;
}

function setText(id, value) {
    document.querySelector(`#${id}`).textContent = value;
}

function renderBooking(booking) {
    currentBooking = booking;
    bookingPanel.hidden = false;
    setText('origin', booking.origin);
    setText('destination', booking.destination);
    setText('passenger', `${booking.firstName} ${booking.surname}`);
    setText('booking-reference', booking.bookingReference);
    setText('flight-number', booking.flightNumber);
    setText('departure-time', new Date(booking.departureTime).toLocaleString());
    setText('seat-number', booking.seatNumber || 'Not assigned');

    const status = document.querySelector('#booking-status');
    status.textContent = booking.status.replace('_', ' ');
    status.className = `status-chip ${booking.status.toLowerCase().replace('_', '-')}`;

    seatChoice.value = booking.seatNumber || '12A';
    seatForm.hidden = booking.status !== 'BOOKED';
    checkInForm.hidden = booking.status !== 'BOOKED';
    document.querySelector('#boarding-pass').hidden = booking.status !== 'CHECKED_IN';

    const note = document.querySelector('#booking-note');
    if (booking.status === 'CANCELLED') {
        note.textContent = 'This booking is cancelled and cannot be checked in.';
    } else if (booking.status === 'CHECKED_IN') {
        note.textContent = 'Check-in is complete. Your demo boarding pass is shown below.';
    } else {
        note.textContent = 'Choose an available seat before completing check-in.';
    }

    if (booking.status === 'CHECKED_IN') {
        setText('pass-origin', booking.origin);
        setText('pass-destination', booking.destination);
        setText('passenger-name', `${booking.firstName} ${booking.surname}`);
        setText('pass-flight', booking.flightNumber);
        setText('pass-departure', new Date(booking.departureTime).toLocaleString());
        setText('pass-seat', booking.seatNumber);
    }
}

lookupForm.addEventListener('submit', async (event) => {
    event.preventDefault();
    bookingPanel.hidden = true;
    showMessage('');
    const reference = document.querySelector('#reference').value.trim();
    const surname = document.querySelector('#surname').value.trim();
    try {
        const booking = await apiRequest(`/api/bookings/${encodeURIComponent(reference)}?surname=${encodeURIComponent(surname)}`);
        renderBooking(booking);
        showMessage('Booking found.', true);
    } catch (error) {
        currentBooking = null;
        showMessage(error.message);
    }
});

seatForm.addEventListener('submit', async (event) => {
    event.preventDefault();
    if (!currentBooking) return;
    showMessage('');
    try {
        const booking = await apiRequest(`/api/bookings/${encodeURIComponent(currentBooking.bookingReference)}/seat`, {
            method: 'PUT',
            body: JSON.stringify({ surname: currentBooking.surname, seatNumber: seatChoice.value }),
        });
        renderBooking(booking);
        showMessage(`Seat ${booking.seatNumber} assigned.`, true);
    } catch (error) {
        showMessage(error.message);
    }
});

checkInForm.addEventListener('submit', async (event) => {
    event.preventDefault();
    if (!currentBooking) return;
    showMessage('');
    try {
        const booking = await apiRequest(`/api/bookings/${encodeURIComponent(currentBooking.bookingReference)}/check-in`, {
            method: 'POST',
            body: JSON.stringify({ surname: currentBooking.surname }),
        });
        renderBooking(booking);
        showMessage('You are checked in. Please review the demonstration boarding pass.', true);
    } catch (error) {
        showMessage(error.message);
    }
});
