import json
import random
import boto3
import secrets
import string
import datetime
import time

USER_POOL_ID = 'replace'
CLIENT_ID = 'replace'

def id_generator(size=6, chars=string.ascii_uppercase + string.digits):
    return ''.join(random.choice(chars) for _ in range(size))
def send_push(sns,device_id, body):
    try:
        try:
            endpoint_response = sns.create_platform_endpoint( PlatformApplicationArn='replace', Token=device_id,)   
            endpoint_arn = endpoint_response['EndpointArn']
        except Exception as err:
            print(err)
            result_re = re.compile(r'Endpoint(.*)already', re.IGNORECASE)
            result = result_re.search(err.message)
            if result:
                endpoint_arn = result.group(0).replace('Endpoint ','').replace(' already','')
            else:
                raise
            

        publish_result = sns.publish(
            TargetArn=endpoint_arn,
            Message=body,
        )
    except :
        var = ""
def lambda_handler(event, context):
    # TODO implement
    if event['action'] == "SignIn":
        return signIn(event['phoneNumber'])
    elif event['action'] == "MatchCode":
        #return event
        return matchCode(event['phoneNumber'],event['code'],event['data'])
    elif event['action'] == "GetPassengerBooked":
        return getPassengerBooked(event['phoneNumber'])
    elif event['action'] == "GetDriverLocation":
        return getDriverLocation(event['phoneNumber'])
    elif event['action'] == "AddDriverLocation":
        return addDriverLocation(event['phoneNumber'],event['location'])
    elif event['action'] == "AddCarOwnerRide":
        return addCarOwnerRide(event['phoneNumber'],event['rideId'])
    elif event['action'] == "GetCarownerStatus":
        return getCarownerStatus(event['phoneNumber'])
    elif event['action'] == "AddPassengerPickUp":
        return addPassengerPickUp(event['city'],event['phoneNumber'],event['passengerLocation'],event['orderTime'],event['destinationName'],event['destination'])
    elif event['action'] == "AddAvailableCars":
        return addAvailableCars(event['data'])
    elif event['action'] == "GetAvailableCars":
        return getAvailableCars(event['phoneNumber'])
    elif event['action'] == "AddUserDevice":
        return getAvailableCars(event['phoneNumber'],event['deviceId'])
    elif event['action'] == "AcceptedDriver":
        return callAcceptedDriver(event['driverNumber'],event['carOwnerNumber'],event['name']
        ,event['time'],event['driverLocation'],event['carOwnerLocation'])
    elif event['action'] == "DriverCarOwnerContract":
        return callDriverCarOwnerContract(event['time'],event['carOwnerNumber'],event['driverNumber'],event['driverLocation'],event['driverActivity'])
   
    elif event['action'] == "GetContract":
        return getContract(event['phoneNumber'])
    elif event['action'] == "addDriverWaiting":
        return addDriverWaiting(event['city'],event['phoneNumber'])
        #addPassengerPickUp(city,phoneNumber,passengerLocation,orderTime,driverLocation,destinationName,destination)
            
    return {
        'statusCode': 200,
        'body': json.dumps('Hello from Lambda!')
    }

def addCarOwnerRide(phoneNumber,rideId):
    dynamodb = boto3.client('dynamodb')
    dynamodb.put_item(TableName='NgilaCarOwnerRides', Item={'RideID': {'S': str(rideId)},'PhoneNumber': {'S': str(phoneNumber)}})
    return "success"

def addDriverLocation(phoneNumber,location):
    dynamodb = boto3.client('dynamodb')
    dynamodb.put_item(TableName='NgilaDriverLocation', Item={'PhoneNumber': {'S': str(phoneNumber)},'Location': {'S': str(location)}})
    return "success"
def book(driverNumber,passengerNumber):
    sns = boto3.client('sns')
    dynamodb = boto3.client('dynamodb')
    print("book "+str(driverNumber)+" to "+str(passengerNumber))
    rideId = id_generator(size=18)
    print("rideId "+str(rideId))
    
    ngilaPassengerBooked = dynamodb.get_item(Key={'PhoneNumber': {'S': passengerNumber}},  TableName='NgilaPassengerBooked')["Item"]
    
    print("ngilaPassengerBooked "+str(ngilaPassengerBooked))
    dynamodb.put_item(TableName='NgilaRides', Item={'RideID': {'S': str(rideId)}
        ,'DriverNumber': {'S': str(driverNumber)}
        ,'PassengerNumber': {'S': str(passengerNumber)}
        ,'PassengerLocation': {'S': str(ngilaPassengerBooked['PassengerLocation']['S'])}
        ,'OrderTime': {'S': str(ngilaPassengerBooked['OrderTime']['S'])}
        ,'DriverLocation': {'S': 'NA'}
        ,"Destination":{'S':ngilaPassengerBooked['Destination']['S']}
        ,'DestinationName': {'S': str(ngilaPassengerBooked['DestinationName']['S'])}
        ,'ArriveTime': {'S': str('0')}
        ,'Distance': {'S': str('0')}
        ,'Charge': {'S': str('0')}})
        
    dynamodb.put_item(TableName='NgilaPassengerRides', Item={'RideID': {'S': str(rideId)},'PhoneNumber': {'S': str(passengerNumber)}})
    dynamodb.put_item(TableName='NgilaDriveRides', Item={'RideID': {'S': str(rideId)},'PhoneNumber': {'S': str(driverNumber)}})
    
    order = {"rideID":rideId
     ,"driverNumber":driverNumber
    ,"passengerNumber":passengerNumber
    ,"destination":ngilaPassengerBooked['Destination']['S']
    ,'passengerLocation': ngilaPassengerBooked['PassengerLocation']['S']
    ,'driverLocation': 'NA'
    ,"destinationName":ngilaPassengerBooked['DestinationName']['S']
    ,"orderTime":ngilaPassengerBooked['OrderTime']['S']
    ,"arriveTime":"-1"
    ,"distance":"0"
    ,"charge":"0"}
    
    bodyJson = json.dumps(order)
    carOwner = dynamodb.get_item(Key={'PhoneNumber': {'S': passengerNumber}},  TableName='NgilaUsersTable')['Item']
    send_push(sns,json.loads(carOwner['Data']['S'].replace("\'", "\""))['deviceId'], bodyJson)
    
    carOwner = dynamodb.get_item(Key={'PhoneNumber': {'S': driverNumber}},  TableName='NgilaUsersTable')['Item']
    send_push(sns,json.loads(carOwner['Data']['S'].replace("\'", "\""))['deviceId'], bodyJson)
    print("book end"+str(driverNumber)+" to "+str(passengerNumber))
    
    
    

    
def addDriverWaiting(city,phoneNumber):
    dynamodb = boto3.client('dynamodb')
    try:
        passengerWaiting =  dynamodb.query( TableName="NgilaPassengerAvailable",  KeyConditionExpression='City = :City', ExpressionAttributeValues={  ':City': {'S': city}})['Items'][0]
        passenger = passengerWaiting['PhoneNumber']['S']
        book(phoneNumber,passenger)
        dynamodb.delete_item(TableName='NgilaPassengerAvailable',Key={'City': {'S': city},'PhoneNumber': {'S': passenger}})
    except:
        dynamodb.put_item(TableName='NgilaDriversAvailable', Item={'City': {'S': str(city)},'PhoneNumber': {'S': str(phoneNumber)}})
    
    
        
    return "success"
    
    
def addPassengerWaiting(city,phoneNumber):
    print("addPassengerWaiting "+city+phoneNumber)
    dynamodb = boto3.client('dynamodb')
    try:
        passengerWaiting = dynamodb.query( TableName="NgilaDriversAvailable",  KeyConditionExpression='City = :City', ExpressionAttributeValues={  ':City': {'S': city}})['Items'][0]
        print(passengerWaiting)
        driver = passengerWaiting['PhoneNumber']['S']
        book(driver,phoneNumber)
        dynamodb.delete_item(TableName='NgilaDriversAvailable',Key={'City': {'S': city},'PhoneNumber': {'S': driver}})
        
    except:
        dynamodb.put_item(TableName='NgilaPassengerAvailable', Item={'City': {'S': str(city)} ,'PhoneNumber': {'S': str(phoneNumber)}})
    
    return "success"
    
def callAcceptedDriver(driverNumber,carOwnerNumber,name,time,driverLocation,carOwnerLocation):
    dynamodb = boto3.client('dynamodb')
    sns = boto3.client('sns')
    availableCars = {"driverNumber":driverNumber,"carOwnerNumber":carOwnerNumber,"name":name
        ,"time":time,"driverLocation":driverLocation,"carOwnerLocation":carOwnerLocation
    }
    bodyJson = json.dumps(availableCars)
    carOwner = dynamodb.get_item(Key={'PhoneNumber': {'S': carOwnerNumber}},  TableName='NgilaUsersTable')['Item']
    print(carOwner)
    send_push(sns,json.loads(carOwner['Data']['S'].replace("\'", "\""))['deviceId'], bodyJson)
    
    #carOwner = dynamodb.get_item(Key={'PhoneNumber': {'S': driverNumber}},  TableName='NgilaUsersTable')['Item']
    #send_push(sns,carOwner['data']['S']['deviceId'], bodyJson)
    return "success"
    
def getContract(time):
    availableCars = []
    dynamodb = boto3.client('dynamodb')
    
    items = dynamodb.query( TableName="NgilaDriverCarOwnerContract",  KeyConditionExpression='CarOwnerNumber = :CarOwnerNumber', ExpressionAttributeValues={  ':CarOwnerNumber': {'S': time}})['Items']
    for item in items:
        availableCars.append({"time":item['Time']['S']
        ,"carOwnerNumber":item['CarOwnerNumber']['S']
        ,"driverNumber":item['DriverNumber']['S']
        ,"driverLocation":item['DriverLocation']['S']
        ,"driverActivity":item['DriverActivity']['S']})
    return availableCars
    
def callDriverCarOwnerContract(time,carOwnerNumber,driverNumber,driverLocation,driverActivity):
    dynamodb = boto3.client('dynamodb')
    sns = boto3.client('sns')
    
    driverCarOwnerContract = {"time":time,"carOwnerNumber":carOwnerNumber,"driverNumber":driverNumber,"driverLocation":driverLocation,"driverActivity":driverActivity}
    
    dynamodb.put_item(TableName='NgilaDriverCarOwnerContract', Item={'Time': {'S': str(time)}
        ,'CarOwnerNumber': {'S': str(carOwnerNumber)}
        ,'DriverNumber': {'S': str(driverNumber)}
        ,'DriverLocation': {'S': str(driverLocation)}
        ,'DriverActivity': {'S': str(driverActivity)}
    })
    bodyJson = json.dumps(driverCarOwnerContract)
    carOwner = dynamodb.get_item(Key={'PhoneNumber': {'S': carOwnerNumber}},  TableName='NgilaUsersTable')['Item']
    send_push(sns,json.loads(carOwner['Data']['S'].replace("\'", "\""))['deviceId'], bodyJson)
    
    carOwner = dynamodb.get_item(Key={'PhoneNumber': {'S': driverNumber}},  TableName='NgilaUsersTable')['Item']
    send_push(sns,json.loads(carOwner['Data']['S'].replace("\'", "\""))['deviceId'], bodyJson)
    return "success"


def getAvailableCars(city):
    availableCars = []
    dynamodb = boto3.client('dynamodb')
    
    items = dynamodb.query( TableName="NgilaAvailableCars",  KeyConditionExpression='City = :City', ExpressionAttributeValues={  ':City': {'S': city}})['Items']
    for item in items:
        availableCars.append({"city":item['City']['S']
        ,"phoneNumber":item['PhoneNumber']['S']
        ,"location":item['Location']['S']
        ,"licensePlate":item['LicensePlate']['S']
        ,"carModel":item['CarModel']['S']
        ,"pickUpTime":item['PickUpTime']['S']
        ,"returnTime":item['ReturnTime']['S']
        ,"locationName":item['LocationName']['S']
        ,"operatingArea":item['OperatingArea']['S']})
    return availableCars
    
    


def addAvailableCars(data):
    dynamodb = boto3.client('dynamodb')
    sns = boto3.client('sns')
    bodyJson = json.dumps(data)
    
    dynamodb = boto3.client('dynamodb')
    dynamodb.put_item(TableName='NgilaAvailableCars', Item={'City': {'S': str(data['city'])}
    ,  'PhoneNumber': {'S': str(data['phoneNumber'])} 
    ,  'Location': {'S': str(data['location'])} 
    ,  'LicensePlate': {'S': str(data['licensePlate'])} 
    ,  'CarModel': {'S': str(data['carModel'])} 
    ,  'PickUpTime': {'S': str(data['pickUpTime'])} 
    ,  'ReturnTime': {'S': str(data['returnTime'])}  
    ,  'LocationName': {'S': str(data['locationName'])}
    ,  'OperatingArea': {'S': str(data['operatingArea'])} })
    
    dynamodb.put_item(Item={'Available': {'S': str('false')},  'PhoneNumber': {'S': str(data['phoneNumber'])} },  TableName='NgilaCarOwners')
     
    #items = dynamodb.query( TableName="NgilaDriversAvailable",  KeyConditionExpression='City = :City', ExpressionAttributeValues={  ':City': {'S': data['city']}})['Items']
    #for item in items:
    #    send_push(sns,item['DeviceId']['S'], bodyJson)
    return "success"
    
     

def getCarownerStatus(phoneNumber,code,data):
    dynamodb = boto3.client('dynamodb')
    
    response = dynamodb.get_item(Key={'PhoneNumber': {'S': phoneNumber}},  TableName='NgilaCarOwners')
    return {"Available":response['Item']['Available'],"PhoneNumber":response['Item']['PhoneNumber']}

#def addDriverAvailable(city,phoneNumber):
#    dynamodb.put_item(TableName='NgilaDriversAvailable', Item={'City': {'S': str(city)},  'PhoneNumber': {'S': str(phoneNumber)}  })
    
def addPassengerPickUp(city,phoneNumber,passengerLocation,orderTime,destinationName,destination):
    dynamodb = boto3.client('dynamodb')
    dynamodb.put_item(
        Item={'PassengerLocation': {'S': passengerLocation}
        ,'OrderTime': {'S': orderTime}
        ,'DestinationName': {'S': destinationName}
        ,'Destination': {'S': destination}
        ,'PhoneNumber': {'S': phoneNumber}
    },  TableName='NgilaPassengerBooked')
    
    
    addPassengerWaiting(city,phoneNumber)   
    return "success"
    #dynamodb.put_item(TableName='NgilaPassengerAvailable', Item={'City': {'S': str(city)},  'PhoneNumber': {'S': str(phoneNumber)}  })
    #dynamodb.put_item(TableName='NgilaPassengerBooked', Item={'City': {'S': str(city)},  'UserPhoneNumber': {'S': str(phoneNumber)} ,  'Location': {'S': str(location)} ,  'Destination': {'S': str(destination)} })
    
def getPassengerPickUp(city):
    dynamodb = boto3.client('dynamodb')
    dynamodb.put_item(TableName='NgilaPassengerPickUps', Item={'City': {'S': str(city)},  'UserPhoneNumber': {'S': str(phoneNumber)} ,  'Location': {'S': str(location)} })
    
def getPassengerBooked(phoneNumber):
    dynamodb = boto3.client('dynamodb')
    
    
    response = dynamodb.get_item(Key={'PhoneNumber': {'S': phoneNumber}},  TableName='NgilaPassengerBooked')
    if(response is not None and response['Item']['DriverNumber'] is not None):
        rideId =response['Item']['RideId']
        driverNumber =response['Item']['DriverNumber']
        items = dynamodb.query( TableName="NgilaDriverTravel",  KeyConditionExpression='DriverNumber = :DriverNumber', ExpressionAttributeValues={  ':DriverNumber': {'S': driverNumber}})['Items']
        
        for item in items:
            return {"driverNumber":item['DriverNumber']['S'],"carOwnerNumber":item['CarOwnerNumber']['S'],
                "location":item['Location']['S'],"passenger":item['Passenger']['S'],"what":item['What']['S'],
                "rideId":item['RideId']['S'],"time":item['Time']['S']
            }
    else:
        ""
        
def getDriverLocation(city):
    locations = []
    dynamodb = boto3.client('dynamodb')
    
    items = dynamodb.query( TableName="NgilaDriverCurrentLocation",  KeyConditionExpression='City = :City', ExpressionAttributeValues={  ':City': {'S': city}})['Items']
    for item in items:
        locations.append({"city":item['City']['S'],"driverNumber":item['DriverNumber']['S'],"location":item['Location']['S']})
    return locations

    
def signIn(phoneNumber):
    
    dynamodb = boto3.client('dynamodb')
    
    opt = random.randint(1000,9999)
    opt=4320
    sns = boto3.client('sns')
    number = phoneNumber
    sns.publish(PhoneNumber = number, Message='Your Pa Ngila verification code is '+str(opt) )
    
    dynamodb.put_item(TableName='GivderOtp', Item={'PhoneNumber': {'S': str(phoneNumber)},  'Code': {'S': str(opt)} })
    return "success"
    
def matchCode(phoneNumber,code,data):
    dynamodb = boto3.client('dynamodb')
    cognito = boto3.client('cognito-idp')
    
    alphabet = string.ascii_letters + string.digits
    password = ''.join(secrets.choice(alphabet) for i in range(20)) 
    
    
    response = dynamodb.get_item(Key={'PhoneNumber': {'S': phoneNumber}},  TableName='GivderOtp')
    
    if response['Item']['Code']['S'] == code:
        try:
            dynamodb.put_item(TableName='NgilaUsersTable', Item={'PhoneNumber': {'S': str(phoneNumber)},  'Data': {'S': str(data)} })
            cognito.sign_up(ClientId=CLIENT_ID,Username=phoneNumber, Password=password)
            cognito.admin_confirm_sign_up( UserPoolId=USER_POOL_ID, Username=phoneNumber)
            resp = cognito.admin_initiate_auth( UserPoolId=USER_POOL_ID,ClientId=CLIENT_ID,AuthFlow='ADMIN_NO_SRP_AUTH',
                 AuthParameters={
                     'USERNAME': phoneNumber,
                     'PASSWORD': password,
                  },
                ClientMetadata={
                  'username': phoneNumber,
                  'password': password,
              })
            dynamodb.put_item(TableName='GivderPasswords',Item={'PhoneNumber': {'S': str(phoneNumber)}, 'Password': {'S': str(password)}})
            return resp['AuthenticationResult']
    
    
        except:
            response = dynamodb.get_item(Key={'PhoneNumber': {'S': phoneNumber}},  TableName='GivderPasswords')['Item']
            print(response)
            password = response['Password']['S']
            resp = cognito.admin_initiate_auth( UserPoolId=USER_POOL_ID,ClientId=CLIENT_ID,AuthFlow='ADMIN_NO_SRP_AUTH',
                 AuthParameters={
                     'USERNAME': phoneNumber,
                     'PASSWORD': password,
                  },
                ClientMetadata={
                  'username': phoneNumber,
                  'password': password,
              })
            return resp['AuthenticationResult']
    else:
        return ""
