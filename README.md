# Mail-Assistant-

This is a mail assisstant to assist Rental Agents when there is a change in tenants.

PROBLEM: When there is a change in tenants a form called an Entry Condition Report must be completed. This form documents the condition of 
the house before the new tenants move in to track whether any damage has been done to the property during the new tenants stay in which 
they will need to compensate for acordingly. To track this, many photos of the property are taken as proof for the condition of the house. 
It is not uncommon to take upwards of 100 photos which the agent then needs to file on their computer with the correct name and folder
placement for potential future use. This can take hours.

SOLUTION: This mail assistant is connected to your email account where you open up the application and press 'et mail' and the rest is 
taken care of. The computer will send a login request to your email where it will then search for emails under a specific subject name
which is chosen by each agent. The computer will then find the photo attached to the email, download it and store it in the correct 
directory on the hard drive using a base directory and then the specific directory of the photo which is found in the email where the agent
will type a short code word for each photo.

For example: The base directory for the application might be "C://Users/John/Desktop/Rental Properties/" and the directory for the photo of the 
email might be "House.Main Bathroom;Crack in bath tub" which is typed into the email. This contains the house, which room and the 
description of the photo. This particular photo would then be stored in the directory "C://Users/Desktop/Rental Properties/House/Bathroom/"
under the name "Crack in bath tub.jpg".

If the directory of the photo does not exist, the application will create it automatically. The application will systemitcally go through 
each email and store each photo. This process can take time so is run on a different thread. When the proccess if complete, the application
will alert the user of the number of emails that were found and succesfully saved.
