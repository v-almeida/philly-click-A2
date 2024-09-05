package br.edu.up.av2

import android.os.Bundle
import androidx.compose.ui.Alignment
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.edu.up.av2.ui.theme.AV2Theme
import kotlin.random.Random

// Classe inicial da atividade
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Define o conteúdo da tela usando Jetpack Compose
        setContent {
            AV2Theme {
                GameScreen() // Chama a função que desenha a tela do jogo
            }
        }
    }
}

// Onde é definido toda a interface do jogo
@Composable
fun GameScreen() {
    // Variáveis que mantêm o estado atual do jogo
    // 'stage' armazena em qual estágio da jornada o jogador está
    var stage by rememberSaveable { mutableStateOf(Stage.INITIAL) }
    // 'clicks' mantém a contagem de cliques do jogador
    var clicks by rememberSaveable { mutableStateOf(0) }
    // 'requiredClicks' é a quantidade de cliques para concluir o jogo, de forma aleatória
    var requiredClicks by rememberSaveable { mutableStateOf(Random.nextInt(1, 51)) }

    // Serve para atualizar o estágio do jogo com base na porcentagem
    LaunchedEffect(clicks, requiredClicks) {
        // Verifica se o jogo não foi concluído ou o jogador não desistiu
        if (stage != Stage.GAVE_UP && stage != Stage.CONQUERED) {
            // Calcula a porcentagem de cliques em relação aos cliques necessários
            val percentage = (clicks.toFloat() / requiredClicks) * 100
            // Atualiza o estágio com base na porcentagem de progresso
            stage = when {
                clicks >= requiredClicks -> Stage.CONQUERED // Jogo concluído
                percentage <= 33 -> Stage.INITIAL // Menos de 33% do progresso
                percentage <= 66 -> Stage.MID // Entre 33% e 66% do progresso
                else -> Stage.FINAL // Mais de 66%, mas ainda não concluído
            }
        }
    }

    // Botão de reiniciar o jogo, seja no final ou após desistir.
    fun resetGame() {
        clicks = 0 // Reseta o número de cliques
        requiredClicks = Random.nextInt(1, 51) // Gera um novo número de cliques necessários
        stage = Stage.INITIAL // Retorna ao estágio inicial
    }

    // Scaffold é uma estrutura básica de layout que facilita a criação da interface
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        content = { innerPadding ->
            // Coluna para organizar os elementos verticalmente no centro da tela
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Função para exibir a imagem correspondente ao estágio atual do jogo
                JourneyImage(stage)

                Spacer(modifier = Modifier.height(16.dp)) // Espaço entre os elementos

                // Exibe diferentes conteúdos com base no estágio do jogo
                when (stage) {
                    // Se o jogador concluiu o jogo
                    Stage.CONQUERED -> {
                        Text("Parabéns! Você concluiu a jornada.")
                        Spacer(modifier = Modifier.height(16.dp))
                        // Botão para reiniciar o jogo
                        Button(onClick = { resetGame() }) {
                            Text("Reiniciar Jogo")
                        }
                    }
                    // Se o jogador desistiu do jogo
                    Stage.GAVE_UP -> {
                        Text("Você desistiu da jornada.")
                        Spacer(modifier = Modifier.height(16.dp))
                        // Botão para reiniciar o jogo
                        Button(onClick = { resetGame() }) {
                            Text("Reiniciar Jogo")
                        }
                    }
                    // Se o jogo está em andamento
                    else -> {
                        // Exibe o progresso atual (cliques/total de cliques necessários)
                        Text("Clique para progredir: $clicks / $requiredClicks")
                        Spacer(modifier = Modifier.height(16.dp))
                        // Botão que incrementa o número de cliques
                        Button(onClick = { clicks++ }) {
                            Text("Clique para avançar")
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        // Botão que permite ao jogador desistir
                        Button(onClick = { stage = Stage.GAVE_UP }) {
                            Text("Desistir")
                        }
                    }
                }
            }
        }
    )
}

// Função que exibe a imagem correspondente ao estágio atual do jogo
@Composable
fun JourneyImage(stage: Stage) {
    // Define a imagem a ser exibida com base no estágio atual
    val imageRes: Painter = when (stage) {
        Stage.INITIAL -> painterResource(id = R.drawable.sixers) // Imagem inicial
        Stage.MID -> painterResource(id = R.drawable.phillies) // Imagem de progresso médio
        Stage.FINAL -> painterResource(id = R.drawable.eagles) // Imagem final antes de concluir
        Stage.CONQUERED -> painterResource(id = R.drawable.trophy) // Imagem de vitória
        Stage.GAVE_UP -> painterResource(id = R.drawable.kdquiter) // Imagem de desistência
    }

    // Exibe a imagem
    Image(
        painter = imageRes,
        contentDescription = "Imagem do estágio $stage", // Descrição da imagem
        modifier = Modifier
            .size(200.dp) // Define o tamanho da imagem
            .clickable { /* Ação ao clicar na imagem, se necessário */ } // Ação clicável, caso precise
    )
}

// Enum que representa os diferentes estágios da jornada no jogo
enum class Stage {
    INITIAL, MID, FINAL, CONQUERED, GAVE_UP
}

// Função para visualizar a tela do jogo no modo de pré-visualização
@Preview(showBackground = true)
@Composable
fun GameScreenPreview() {
    AV2Theme {
        GameScreen() // Chama a tela do jogo para visualização
    }
}
