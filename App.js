import * as React from "react";
import { Button, View, Text, NativeModules, NativeEventEmitter } from "react-native";
import { NavigationContainer } from "@react-navigation/native";
import { createNativeStackNavigator } from "@react-navigation/native-stack";
const { InteractionStudioModule } = NativeModules;

class HomeScreen extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            param1: ""
        };
    }

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

    render() {
        return (
            <View style={{ flex: 1, alignItems: "center", justifyContent: "center" }}>
                <Button
                    title="Click to send onClick event to Interaction Studio"
                    onPress={() => {
                        InteractionStudioModule.onClick();
                    }}
                />
                <Text>Campaign response param1: {this.state.param1}</Text>
            </View>
        );
    }
}

const Stack = createNativeStackNavigator();

function App() {
    return (
        <NavigationContainer>
            <Stack.Navigator initialRouteName="Home">
                <Stack.Screen name="Home" component={HomeScreen} />
            </Stack.Navigator>
        </NavigationContainer>
    );
}

export default App;
