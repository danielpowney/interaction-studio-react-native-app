import React, {useRef} from "react";
import { Button, View, Text, NativeModules, NativeEventEmitter } from "react-native";
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
                routeNameRef.current = navigationRef.getCurrentRoute().name;
            }}
            onStateChange={async () => {
                /*
                 * If screen changes, call native module to send event to Interaction Studio
                 */
                const previousRouteName = routeNameRef.current;
                const currentRouteName = navigationRef.getCurrentRoute().name;
                if (previousRouteName !== currentRouteName) {
                    InteractionStudioModule.viewScreen(currentRouteName);
                }
                routeNameRef.current = currentRouteName;
            }}
        >
            <Stack.Navigator initialRouteName="Home">
                <Stack.Screen name="Home" component={HomeScreen} />
                <Stack.Screen name="Detail" component={DetailScreen} />
            </Stack.Navigator>
        </NavigationContainer>
    );
}

/**
 * Detail screen
 */
function DetailScreen({ navigation: { goBack } }) {
    return (
        <View style={{ flex: 1, alignItems: "center", justifyContent: "center" }}>
            <Button title="Back to Home" onPress={() => goBack() } />
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
            param1: ""
        };
    }

    /**
      * When the component is ready, add an event listener to handle campaign responses
      */ 
    componentDidMount() {
        const eventEmitter = new NativeEventEmitter(InteractionStudioModule);
        this.eventListener = eventEmitter.addListener("my_campaign_response", (eventData) => {
            this.setState({
                param1: eventData.param1
            });
        });
    }

    componentWillUnmount() {
        this.eventListener.remove(); //Removes the listener
    }

    /**
     * When the button is clicked, a call is made to the native module to send an
     * event to Interaction Studio. The UI will be re-rendered asyncrhonously with 
     * any campaign responses if the Rezct component state changes.
     */
    render() {
        return (
            <View style={{ flex: 1, alignItems: "center", justifyContent: "center" }}>
                <Button
                    title="Click Me"
                    onPress={() => {
                        InteractionStudioModule.homeBtnClick();
                    }}
                />
                <Text>Campaign response param1: {this.state.param1}</Text>
                
                <Button
                    title="Go to Details screen"
                    onPress={() => {
                        this.props.navigation.navigate("Detail");
                    }}
                />
            </View>
        );
    }
}

export default App;
