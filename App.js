import React, {useRef} from "react";
import { Button, View, Text, NativeModules, NativeEventEmitter, Alert, Image } from "react-native";
import { NavigationContainer, useNavigationContainerRef } from "@react-navigation/native";
import { createNativeStackNavigator } from "@react-navigation/native-stack";

const { InteractionStudioModule } = NativeModules;
const Stack = createNativeStackNavigator();

/**
 * Simple app with 2 screens. The home screen has a button which sends an 
 * event to Interaction Studio when clicked and update the UI with campaign response data
 */
function App() {

    const navigationRef = useNavigationContainerRef();
    const routeNameRef = useRef();

    return (
        <NavigationContainer
            ref={navigationRef}
            onReady={() => {
                const currentRouteName = navigationRef.getCurrentRoute().name;
                routeNameRef.current = currentRouteName;
                InteractionStudioModule.viewScreen(currentRouteName, null); // initial view
                console.log("View " + currentRouteName);
            }}
            onStateChange={async () => {
                /*
                 * If screen changes, call native module to send event to Interaction Studio
                 */
                const previousRouteName = routeNameRef.current;
                const currentRouteName = navigationRef.getCurrentRoute().name;
                if (previousRouteName !== currentRouteName) {

                    if (currentRouteName === "Product") { // View Product
                        let productId = "abc"; // lookup product id
                        InteractionStudioModule.viewScreen(currentRouteName, productId);
                    } else if (currentRouteName === "Category") { // View Category
                        let categoryId = "abc"; // lookup category id
                        InteractionStudioModule.viewScreen(currentRouteName, categoryId);
                    } else {
                        InteractionStudioModule.viewScreen(currentRouteName, null);
                    }
                    console.log("View " + currentRouteName);

                }
                routeNameRef.current = currentRouteName;
            }}
        >
            <Stack.Navigator initialRouteName="Home">
                <Stack.Screen name="Home" component={HomeScreen} />
                <Stack.Screen name="Product" component={ProductScreen} />
                 <Stack.Screen name="Category" component={CategoryScreen} />
            </Stack.Navigator>
        </NavigationContainer>
    );
}

/**
 * Category screen
 */
function CategoryScreen({ navigation: { goBack, navigate } }) {
    return (
        <View style={{ flex: 1, alignItems: "center", justifyContent: "center" }}>
            <Button title="Go Back" onPress={() => goBack() } />
        </View>
    );
}

/**
 * Product screen with an add to cart button
 */
function ProductScreen({ navigation: { goBack, navigate } }) {
    return (
        <View style={{ flex: 1, alignItems: "center", justifyContent: "center" }}>
            <Button title="Add to Cart" onPress={() => {
                let productId = "abc"; // lookup product id
                let quantity = 1; // set quantity
                InteractionStudioModule.addToCart(productId, quantity, () => {
                    Alert.alert( null, "Product has been added to cart." );
                });
            } }
            />
            <Button title="View Category" onPress={() => navigate('Category') } />
            <Button title="Go to Home" onPress={() => navigate('Home') } />
        </View>
    );
}

/**
 * Home screen
 */
class HomeScreen extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            headerText: null,
            imageURL: null
        };
    }

    /**
      * When the component is ready, add an event listener to handle home banner 1 campaign response
      */ 
    componentDidMount() {
        const eventEmitter = new NativeEventEmitter(InteractionStudioModule);
        this.eventListener = eventEmitter.addListener("homeBanner1_ready", (eventData) => {
            console.log(eventData.imageURL);
            this.setState({
                imageURL: eventData.imageURL,
                headerText : eventData.headerText
            });
        });
    }

    componentWillUnmount() {
        this.eventListener.remove(); //Removes the listener
    }

    /**
     * Renders component
     */
    render() {
        let isLoading = ( this.state.imageURL == null || this.state.headerText == null );
        return (
            <View style={{ flex: 1, alignItems: "center", justifyContent: "center" }}>
                { isLoading ? 
                    ( <Text style={{marginBottom: 5}}>Loading...</Text> ) : 
                    ( 
                        <>
                            <Text style={{ fontWeight: 'bold', fontSize: 24, marginBottom: 5 }}>{this.state.headerText}</Text>
                            <Image source={{ uri: this.state.imageURL }} style={{ resizeMode: 'cover', width: '100%', height: 200, marginBottom: 5 }} />
                        </>
                    )
                }
                <Button
                    title="Go to Product"
                    onPress={() => {
                        this.props.navigation.navigate("Product");
                    }}
                />
            </View>
        );
    }
}

export default App;