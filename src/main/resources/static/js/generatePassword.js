function generatePassword() {
    //Stores the html elements in a constant (essentially variable/attribute in java)
    const passwordInput = document.getElementById('generatedPassword');
    const notification = document.getElementById('passwordNotification');
    const endpointUrl = '/account/generatePassword';

    //Updates the notification div that it is generating a password
    notification.textContent = 'Generating...';

    try {
        const request = new XMLHttpRequest();

        request.open('GET', endpointUrl, false);
        request.send(null);

        if (request.status !== 200) {
            throw new Error(`Request failed with status ${request.status}`);
        }

        const generatedPassword = request.responseText;

        passwordInput.value = generatedPassword;

        navigator.clipboard.writeText(generatedPassword);

        notification.textContent = 'New password generated and copied to clipboard';
    } catch (error) {
        console.error('Password generation failed:', error);
        notification.textContent = 'Failed to generate password.';
    }
}