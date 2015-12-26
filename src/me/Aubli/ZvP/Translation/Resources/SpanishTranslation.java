package me.Aubli.ZvP.Translation.Resources;

import java.util.Locale;

import me.Aubli.ZvP.Translation.LanguageBundle;
import me.Aubli.ZvP.Translation.MessageKeys.arena;
import me.Aubli.ZvP.Translation.MessageKeys.category;
import me.Aubli.ZvP.Translation.MessageKeys.commands;
import me.Aubli.ZvP.Translation.MessageKeys.config;
import me.Aubli.ZvP.Translation.MessageKeys.dataType;
import me.Aubli.ZvP.Translation.MessageKeys.error;
import me.Aubli.ZvP.Translation.MessageKeys.game;
import me.Aubli.ZvP.Translation.MessageKeys.inventory;
import me.Aubli.ZvP.Translation.MessageKeys.manage;
import me.Aubli.ZvP.Translation.MessageKeys.status;

import org.bukkit.ChatColor;


public class SpanishTranslation extends LanguageBundle {
    
    @Override
    public Locale getLocale() {
	return new Locale("es");
    }
    
    @Override
    public String getAuthor() {
	return "zuhir";
    }
    
    @Override
    protected Object[][] getContents() {
	return this.contents;
    }
    
    //@formatter:off
    private Object[][] contents = {
	   
	    {config.reloaded.name(),  ChatColor.GREEN + "Configuracion recargada con exito!"},
	    {commands.missing_permission.name(), ChatColor.DARK_RED + "No tienes permiso para hacer eso!"},
	    {commands.no_commands_allowed.name(), ChatColor.DARK_RED + "No puedes ejecutar comandos mientras estas en una arena de ZvP!"},
	    {commands.only_for_Players.name(), "Solo los jugadores pueden usar ese comando!"},
	    
	    {game.waiting_for_players.name(), ChatColor.DARK_GRAY + "Esperando jugadores ..."},
	    {game.joined.name(), ChatColor.GREEN + "Te has unico a la arena " + ChatColor.GOLD + "%s " + ChatColor.GREEN + "!"},
	    {game.left.name(), ChatColor.GREEN + "Has salido de la arena " + ChatColor.GOLD + "%s " + ChatColor.GREEN + "!"},
	    {game.player_left.name(), ChatColor.DARK_GRAY + "El jugador " + ChatColor.GOLD + "%s" + ChatColor.DARK_GRAY + " se fue del juego!"},
	    {game.player_joined.name(), ChatColor.DARK_GRAY + "El jugador " + ChatColor.GOLD + "%s" + ChatColor.DARK_GRAY + " se unio al juego!"},
	    {game.player_not_found.name(), ChatColor.RED + "No se encontraron partidas (Podria ser un error del plugin)!"},
	    {game.player_died.name(), ChatColor.DARK_GRAY + "El jugador " + ChatColor.GOLD + "%s" + ChatColor.DARK_GRAY + " murio!"},
	    {game.player_bought.name(), ChatColor.DARK_GRAY + "El jugador " + ChatColor.GOLD + "%s" + ChatColor.DARK_GRAY + " compro " + ChatColor.GOLD + "%s" + ChatColor.DARK_GRAY + " por " + ChatColor.GOLD + "%s" + ChatColor.DARK_GRAY + " !"},
	    {game.player_bought_more.name(), ChatColor.DARK_GRAY + "El jugador " + ChatColor.GOLD + "%s" + ChatColor.DARK_GRAY + " compro " + ChatColor.GOLD + "%s %s" + ChatColor.DARK_GRAY + " por "+ ChatColor.GOLD + "%s" + ChatColor.DARK_GRAY + " !"},
	    {game.player_sold.name(), ChatColor.DARK_GRAY + "El jugador " + ChatColor.GOLD + "%s" + ChatColor.DARK_GRAY + " vendio " + ChatColor.GOLD + "%s" + ChatColor.DARK_GRAY + " por " + ChatColor.GOLD + "%s" + ChatColor.DARK_GRAY + " !"},
	    {game.player_sold_more.name(), ChatColor.DARK_GRAY + "El jugador " + ChatColor.GOLD + "%s" + ChatColor.DARK_GRAY + " vendio " + ChatColor.GOLD + "%s %s" + ChatColor.DARK_GRAY + " por " + ChatColor.GOLD + "%s" + ChatColor.DARK_GRAY + "!"},
	    {game.player_bought_kit.name(), ChatColor.GREEN + "Has comprado un kit " + ChatColor.GOLD + "%s" + ChatColor.GREEN + " por " + ChatColor.GOLD + "%s" + ChatColor.GREEN + "! Tienes " + ChatColor.GOLD + "%s" + ChatColor.GREEN + " restante(s)!"},
	    {game.no_item_to_sell.name(), ChatColor.RED + "No tienes este objeto!"},
	    {game.not_in_game.name(), ChatColor.RED + "No estas en una partida!"},
	    {game.already_in_game.name(), ChatColor.RED + "Ya estas en una partida!"},
	    {game.vote_request.name(), ChatColor.DARK_PURPLE + "Escribe '" + ChatColor.GOLD + "zvp vote" + ChatColor.DARK_PURPLE + "' para votar por la siguiente ronda!"},
	    {game.voted_next_wave.name(), ChatColor.GREEN + "Has votado por la siguiente ronda!"},
	    {game.already_voted.name(), ChatColor.RED + "Ya has votado por la siguiente ronda!"},
	    {game.no_voting.name(), ChatColor.RED + "No es hora de votar!"},
	    {game.feature_disabled.name(), ChatColor.RED + "Esta funcion esta desactivada!"},
	    {game.spawn_protection_enabled.name(), ChatColor.GREEN + "Eres invencible por " + ChatColor.GOLD + "%s" + ChatColor.GREEN + " segundos!"},
	    {game.spawn_protection_over.name(), ChatColor.RED + "ATENCION! " + ChatColor.GREEN + "Ya no eres invencible!"},
	    {game.won.name(), ChatColor.GOLD + "" + ChatColor.BOLD + "Felicidades!\n" + ChatColor.RESET + ChatColor.DARK_GRAY + "Le has ganado a los zombies. Has luchado contra " + ChatColor.GOLD + "%s" + ChatColor.DARK_GRAY + " zombies en " + ChatColor.GOLD + "%s" + ChatColor.DARK_GRAY + " ronda(s) y has muerto " + ChatColor.GOLD + "%s" + ChatColor.DARK_GRAY + "  veces. Los restos de dinero adquirido " + ChatColor.GOLD + "( %s )" + ChatColor.DARK_GRAY + " seran donados a la " + ChatColor.GOLD + "%s" + ChatColor.DARK_GRAY + "." + ChatColor.BOLD + ChatColor.DARK_GREEN + " Gracias por jugar!"},
	    {game.won_messages.name(), "Hospital de Notch;Fabrica de Minecraft;Granjas de Creepers;Academia de Jeb"},
	    {game.lost.name(), ChatColor.DARK_GRAY + "Has perdido el juego!" + ChatColor.RED + " Todo tu equipo esta muerto!" + ChatColor.DARK_GRAY + " Has asesinado " + ChatColor.GOLD + "%s" + ChatColor.DARK_GRAY + " zombies en " + ChatColor.GOLD + "%s" + ChatColor.DARK_GRAY + " ronda(s)! " + ChatColor.GREEN + "" + ChatColor.BOLD + "Mejor suerte la proxima partida!"},
	    {game.spectator_mode.name(), ChatColor.GOLD + "Ahora te encuentras en modo espectador. Puedes utilizar las herramientas de tu inventario."},
	    {game.speedTool_description.name(), ChatColor.GREEN + "Usa esta herramienta para cambiar tu velocidad de vuelo."},
	    {game.speedTool_enabled.name(), ChatColor.GREEN + "Ahora eres mas veloz"},
	    {game.speedTool_disabled.name(), ChatColor.RED + "Ahora tienes la velocidad basica."},
	    {game.teleportTool_description.name(), ChatColor.GREEN + "Usa esta herramienta para teletransportarte a los jugadores vivos."},
	    {game.teleport_to.name(), ChatColor.DARK_GRAY + "Teletransportado a " + ChatColor.GOLD + "%s" + ChatColor.DARK_GRAY + "!"},
	    
	    {arena.stop_all.name(), ChatColor.DARK_GRAY + "Todas las arenas se han detenido!"},
	    {arena.stop.name(), ChatColor.DARK_GRAY + "La arena " + ChatColor.GOLD + "%s" + ChatColor.DARK_GRAY + " ha sido detenida!"},
	    {arena.offline.name(), ChatColor.RED + "La arena esta apagada!"},
	    {arena.not_ready.name(), ChatColor.RED + "La arena no esta lista o no esta llena!"},
	    
	    {manage.right_saved.name(), ChatColor.GREEN + "Click derecho en guardar!"},
	    {manage.left_saved.name(), ChatColor.GREEN + "Click izquierdo en guardar!"},
	    {manage.position_saved.name(), ChatColor.GOLD + "%s " + ChatColor.GREEN + "guardado!"},
	    {manage.position_saved_poly.name(), ChatColor.GREEN + "La posiciom " + ChatColor.GOLD + "%s" + ChatColor.GREEN + " ha sido guardado! Usa " + ChatColor.DARK_PURPLE + "'/zvp add arena clear'" + ChatColor.GREEN + " para limpiar esta lista! Usa " + ChatColor.DARK_PURPLE + "'/zvp add arena finish'" + ChatColor.GREEN + " para finalizar la edicion!"},
	    {manage.position_cleared.name(), ChatColor.GREEN + "Las posiciones se han reseteado! Puedes empezar de nuevo!"},
	    {manage.position_not_saved.name(), ChatColor.RED + "Esta posicion no se puede guardar!"},
	    {manage.position_not_in_arena.name(), ChatColor.RED + "Esta posicion no es parte de la arena!"},
	    {manage.tool.name(), ChatColor.DARK_PURPLE + "Usa esta herramienta para crear posiciones en la arena!"},
	    {manage.lobby_saved.name(), ChatColor.GREEN + "Lobby guardado!"},
	    {manage.arena_saved.name(), ChatColor.GREEN + "La arena " + ChatColor.GOLD + "%s" + ChatColor.GREEN + " se ha guardado!"},
	    {manage.lobby_removed.name(), ChatColor.GREEN + "Lobby eliminado de la configuracion!"},
	    {manage.arena_removed.name(), ChatColor.GREEN + "Arena eliminada de la configuracion!"},
	    {manage.arena_status_changed.name(), ChatColor.GREEN + "Ahora es " + ChatColor.GOLD + "%s" + ChatColor.GREEN + "!"},
	    {manage.sign_saved.name(), ChatColor.GREEN + "Cartel colocado correctamente!"},
	    {manage.sign_removed.name(), ChatColor.GREEN + "Cartel removido correctamente!"},
	    {manage.kit_saved.name(), ChatColor.GREEN + "El kit " + ChatColor.GOLD + "%s" + ChatColor.GREEN + " ha sido guardado correctamente!"},
	    {manage.kit_removed.name(), ChatColor.GREEN + "El kit " + ChatColor.GOLD + "%s" + ChatColor.GREEN + " ha eliminado correctamente!"},
	    {manage.record_start.name(), ChatColor.GREEN + "Las estadisticas separadas seran grabadas en las siguientes " + ChatColor.GOLD + "%s" + ChatColor.GREEN + " horas!"},
	    {manage.record_already_running.name(), ChatColor.RED + "Un diferente record esta actualmente en ejecucion. Solo puede haber 1 a la vez!"},
	    
	    {error.sign_remove.name(), ChatColor.RED + "Ha ocurrido un error al remover el cartel!"},
	    {error.sign_place.name(), ChatColor.RED + "Ha ocurrido un error al colocar el cartel!"},
	    {error.sign_layout.name(), ChatColor.RED + "El cartel esta mal escrito!"},
	    {error.arena_place.name(), ChatColor.RED + "Las posiciones establecidas no estan en el mismo mundo!"},
	    {error.prelobby_add.name(), ChatColor.RED + "Ha ocurrido un error al guardar el lobby!"},
	    {error.no_prelobby.name(), ChatColor.RED + "Esta arena no tiene lobby!"},
	    {error.lobby_not_available.name(), ChatColor.RED + "Este lobby no esta disponible!"},
	    {error.arena_not_available.name(),ChatColor.RED + "La arena no esta disponible!"},
	    {error.kit_already_exists.name(), ChatColor.RED + "El kit " + ChatColor.GOLD + "%s" + ChatColor.RED + " ya existe! Escoge otro nombre!"},
	    {error.kit_not_exist.name(), ChatColor.RED + "El kit " + ChatColor.GOLD + "%s" + ChatColor.RED + " no existe!"},
	    {error.transaction_failed.name(), ChatColor.RED + "La transaccion fracaso!"},
	    {error.no_money.name(), ChatColor.RED + "No tienes suficiente dinero!"},
	    {error.wrong_inventory.name(), "Has clickeado mal el inventario! Prueba el de arriba!"},
	    {error.record_start_error.name(), ChatColor.RED + "Ha ocurrido un error! No se inicio Record!"},
	    {error.negative_duration.name(), ChatColor.RED + "La duracion debe ser mayor a 0!"},
	    
	    {category.food.name(), "Comida"},
	    {category.armor.name(), "Armadura"},
	    {category.weapon.name(), "Armas"},
	    {category.potion.name(), "Pociones"},
	    {category.misc.name(), "Otros"},
	    
	    {dataType.kills.name(), "Asesinatos"},
	    {dataType.kill_record.name(), "Record de asesin"}, //INFO asesinatos replaced with asesin, sign only allows 15 letters
	    {dataType.deaths.name(), "Muertes"},
	    {dataType.left_money.name(), "Dinero restante"},
	    
	    {status.running.name(), "Encendida"},
	    {status.waiting.name(), "Esperando..."},
	    {status.stoped.name(), "Apagada"},
	    
	    {inventory.kit_select.name(), "Selecciona tu kit!"},
	    {inventory.place_icon.name(), "Pon el icono del kit aqui!"},
	    {inventory.select_category.name(), "Selcciona la categoria"},
	    {inventory.select_recordType.name(), "Selecciona el tipo de estadisticas"},
	    {inventory.living_players.name(), "Jugadores vivos"},
    };
}
