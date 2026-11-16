# ZYNDRA MOUSE PRO — Alpha 0.1.0

Aplicativo Android independente, com identidade visual roxo neon. O Telegram fica indisponível; o único canal social configurado é o Discord:
https://discord.gg/2fBH8ud3Ur

## O que já existe nesta base
- Painel inicial em português.
- Lista de aplicativos instalados (o usuário escolhe um para abrir).
- Detecção básica de dispositivos físicos de teclado e mouse.
- Editor inicial de mapeamentos W/A/S/D, Espaço, F e E para coordenadas percentuais.
- Serviço de acessibilidade experimental que transforma as teclas configuradas em gestos de toque.
- Atalho flutuante que abre o app.
- Configurações e link único do Discord.
- Workflow do GitHub Actions para compilar APK debug.

## Limitações importantes
Esta é uma **base alfa**, não uma cópia funcional completa do GG Mouse Pro. O mapeamento atual é experimental: usa gestos de acessibilidade, não captura mouse completo nem oferece joystick analógico, macros ou compatibilidade universal. Alguns jogos podem ignorar/bloquear gestos sintetizados, exigir suporte próprio ou não funcionar com esse método. Não use para contornar anticheat, regras de jogos ou proteções do Android. Teste em jogos e apps que permitam esse tipo de controle.

A permissão de acessibilidade é poderosa. Ative o serviço apenas se compreender o que ele faz. O serviço deste projeto não envia dados para servidores; os perfis ficam localmente nas preferências do app.

`QUERY_ALL_PACKAGES` é usado para mostrar a lista de apps instalados. Essa permissão pode não ser aceita em distribuição pela Google Play; para publicação pública, revise a política vigente e considere uma lista limitada de jogos ou distribuição direta.

## Compilar no computador (Android Studio)
1. Instale Android Studio e JDK 17.
2. Extraia o ZIP.
3. Abra a pasta `ZYNDRA_MOUSE_PRO` no Android Studio.
4. Aguarde a sincronização do Gradle.
5. Se solicitado, instale Android SDK Platform 35.
6. Selecione **Build > Build Bundle(s) / APK(s) > Build APK(s)**.
7. O APK debug ficará em `app/build/outputs/apk/debug/app-debug.apk`.

## Compilar sem PC usando GitHub Actions
1. Crie um repositório GitHub vazio.
2. Envie todo o conteúdo desta pasta para a branch `main` (incluindo `.github/workflows/build-apk.yml`).
3. Abra a aba **Actions** e execute **Build ZYNDRA MOUSE PRO APK** (ou faça push na `main`).
4. Quando terminar, abra a execução e baixe o artefato `ZYNDRA-MOUSE-PRO-debug`.
5. Extraia o artefato para obter `app-debug.apk`.

O workflow configura Gradle 8.9 no runner. Não há wrapper Gradle incluído nesta entrega.

## Gerar link de download
Opção simples: crie uma GitHub Release no repositório, anexe `app-debug.apk` e publique. O GitHub mostrará um link de download do arquivo. Para um link direto, use o endereço do asset da release. Não publique APKs de terceiros nem compartilhe builds sem testar.

## Instalar no Android
1. Baixe o APK.
2. Abra-o e autorize a instalação desta fonte, se o Android solicitar.
3. Após instalar, abra o app.
4. Para mapear teclas, abra **Configurações > Acessibilidade > Serviços instalados > ZYNDRA MOUSE PRO - Mapeador** e ative conscientemente.
5. Para o botão flutuante, permita **Exibir sobre outros apps**.
6. Conecte teclado/mouse compatíveis por USB-OTG ou Bluetooth e teste primeiro fora de jogos.

## Identidade
- Nome: ZYNDRA MOUSE PRO
- Pacote: `com.zyndra.mousepro`
- Tema: preto + roxo neon
- Redes: somente Discord `https://discord.gg/2fBH8ud3Ur`
- Telegram: indisponível
