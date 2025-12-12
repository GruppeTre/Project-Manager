async function generatePassword() {
    //Stores the html elements in a constant (essentially variable/attribute in java)
    const passwordInput = document.getElementById('generatedPassword');
    const notification = document.getElementById('passwordNotification');
    const endpointUrl = '/account/generatePassword';

    //Updates the notification div that it is generating a password
    notification.textContent = 'Generating...';

    try {
        //Calls the spring endpoint
        const response = await fetch(endpointUrl, {
            method: 'GET'
        });

        //Throws if you're not logged in or is not an admin in the endpoint (prevents malicious intent)
        if (!response.ok) {
            throw new Error(`Authentication/Authorization error: ${response.status}`);
        }

        //Saves the generated password
        const generatedPassword = await response.text();


        //Sets the password input
        passwordInput.value = generatedPassword;


        //Copies the password to clipboard and notifies the user
        await navigator.clipboard.writeText(generatedPassword);

        notification.textContent = 'New password generated and copied to clipboard! Ensure that it is securely delivered to the employee';

        //Removes the notification box
        setTimeout(() => {
            notification.textContent = '';
        }, 3000);

    } catch (error) {
        console.error('Password generation failed:', error);
        notification.textContent = 'Failed to generate password.';
    }
}