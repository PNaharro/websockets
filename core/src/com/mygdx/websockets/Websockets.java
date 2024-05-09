package com.mygdx.websockets;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.net.HttpRequestBuilder;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.Timer;

public class Websockets extends ApplicationAdapter {
	Dialog dialog;
	Skin skin;
	Stage stage;
	Label ip;

	@Override
	public void create() {
		skin = new Skin(Gdx.files.internal("uiskin.json"));

		// Ajusta el tamaño del texto
		skin.getFont("default-font").getData().setScale(1.5f);

		stage = new Stage();

		Gdx.input.setInputProcessor(stage);

		dialog = new Dialog("", skin) {
			protected void result(Object object) {
				System.out.println("Option: " + object);

				Net.HttpResponseListener listener = new Net.HttpResponseListener() {
					@Override
					public void handleHttpResponse(Net.HttpResponse httpResponse) {
						// Obtener el texto de la respuesta HTTP
						String responseText = httpResponse.getResultAsString();

						// Crear un lector JSON para analizar la respuesta
						JsonReader jsonReader = new JsonReader();
						JsonValue jsonResponse = jsonReader.parse(responseText);

						// Obtener los valores específicos del JSON
						String ipValue = jsonResponse.getString("ip");
						String countryValue = jsonResponse.getString("country");
						String ccValue = jsonResponse.getString("cc");

						// Crear el texto que deseas mostrar en la Label
						String labelText = "IP: " + ipValue + "\n Country: " + countryValue + "\n CC: " + ccValue;

						// Establecer el texto en la Label
						ip.setText(labelText);
					}

					@Override
					public void failed(Throwable t) {
						System.out.println("ERROR: " + t.toString());
					}

					@Override
					public void cancelled() {
						System.out.println("CANCELLED");
					}
				};

				if (object.equals(1L)) {
					HttpRequestBuilder requestBuilder = new HttpRequestBuilder();
					Net.HttpRequest httpRequest = requestBuilder.newRequest().method(Net.HttpMethods.GET).url("http://api.myip.com").build();
					Gdx.net.sendHttpRequest(httpRequest, listener);
				}
				Timer.schedule(new Timer.Task() {
					@Override
					public void run() {
						dialog.show(stage);
					}
				}, 1);
			}
		};


		// Agrega un campo de texto al diálogo
		ip = new Label("Presiona el boton", skin);
		ip.setFontScale(3); // Ajusta el tamaño del texto
		dialog.getContentTable().add(ip).pad(20).height(400).width(600).row();
		dialog.setColor(1, 0, 0, 1);

		// Crea un botón con texto más grande
		TextButton httpButton = new TextButton(" HTTP ", skin);
		httpButton.getLabel().setFontScale(3); // Ajusta el tamaño del texto del botón
		httpButton.getSkin().setScale(3);
		dialog.button(httpButton, 1L);


		Timer.schedule(new Timer.Task() {
			@Override
			public void run() {
				dialog.show(stage);
			}
		}, 1);

	}

	@Override
	public void render() {
		Gdx.gl.glClearColor(0, 0, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		stage.act();
		stage.draw();
	}

	@Override
	public void dispose() {
		stage.dispose();
	}
}
