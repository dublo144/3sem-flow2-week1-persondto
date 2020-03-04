document.addEventListener('DOMContentLoaded', (event) => {
    fetchAndPopulatate();
});

const fetchAndPopulatate = () => {
    fetch('api/person')
        .then(res => res.json())
        .then(data => data.all)
        .then(data => populateTable(data))
};

const populateTable = data => {
    const dataArray = data.map(person => `<tr> <td>${person.id}</td> <td>${person.firstName}</td> <td>${person.lastName}</td> <td>${person.phone}</td></tr>`);
    document.getElementById('tableBody').innerHTML = dataArray.join('');
};

const submitNewPerson = () => {
    const firstName = document.getElementById('firstNameInput').value;
    const lastName = document.getElementById('lastNameInput').value;
    const phone = document.getElementById('phoneInput').value;
    const street = document.getElementById('streetInput').value;
    const zip = document.getElementById('zipInput').value;
    const city = document.getElementById('cityInput').value;

    const address = {
        "street": street,
        "zip": zip,
        "city": city
    };

    const newPersonObj = [{
        "firstName": firstName,
        "lastName":lastName,
        "phone":phone,
        "address": address
    }];

    const headers = new Headers();
    headers.set('Content-Type', 'application/json');

    const url = 'api/person/add';
    const fetchOptions = {
        method: 'POST',
        headers,
        body: JSON.stringify(newPersonObj)
    };

    fetch(url, fetchOptions)
        .then(res => res.json())
        .then(data => console.log(data))
};