package web.controlevacinacao.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import web.controlevacinacao.filter.VacinaFilter;
import web.controlevacinacao.model.Status;
import web.controlevacinacao.model.Vacina;
import web.controlevacinacao.pagination.PageWrapper;
import web.controlevacinacao.repository.VacinaRepository;
import web.controlevacinacao.service.VacinaService;

@Controller
@RequestMapping("/vacinas")
public class VacinaController {

    private static final Logger logger = LoggerFactory.getLogger(VacinaController.class);

    private VacinaRepository vacinaRepository;
    private VacinaService vacinaService;

    public VacinaController(VacinaRepository vacinaRepository, VacinaService vacinaService) {
        this.vacinaRepository = vacinaRepository;
        this.vacinaService = vacinaService;
    }

    @GetMapping("/todas")
    public String mostrarTodasVacinas(Model model) {
        List<Vacina> vacinas = vacinaRepository.findAll();
        logger.info("Vacinas buscadas: {}", vacinas);
        model.addAttribute("vacinas", vacinas);
        return "vacinas/todas";
    }

    @GetMapping("/cadastrar")
    public String abrirPaginaCadastro(Vacina vacina) {
        return "vacinas/cadastro";
    }

    @GetMapping(value = "/cadastrar", headers = "HX-Request")
    public String abrirCadastroVacinaHTMX(Vacina vacina) {
        return "vacinas/cadastro :: formulario";
    }

    @PostMapping(value = "/cadastrar", headers = "HX-Request")
    public String cadastrarVacinaHTMX(Vacina vacina, HttpServletResponse response) {
        vacinaService.salvar(vacina);
        response.setHeader("HX-Location",
                "{\"path\":\"/vacinas/sucesso\", \"target\":\"#main\", \"swap\":\"outerHTML\"}");
        return "mensagem";
    }

    @GetMapping(value = "/sucesso", headers = "HX-Request")
    public String abrirMensagemSucessoHTMX(Model model) {
        model.addAttribute("mensagem", "Vacina cadastrada com sucesso");
        return "mensagem :: texto";
    }

    @PostMapping("/cadastrar")
    public String cadastrar(Vacina vacina) {
        vacinaService.salvar(vacina);
        return "redirect:/vacinas/sucesso";
    }

    @GetMapping("/sucesso")
    public String abrirSucesso(Model model) {
        model.addAttribute("mensagem", "Cadastro de Vacina Efetuado com Sucesso");
        return "mensagem";
    }

    @GetMapping("/abrirpesquisar")
    public String abrirPaginaPesquisa() {
        return "vacinas/pesquisar";
    }

    @GetMapping(value = "/abrirpesquisar", headers = "HX-Request")
    public String abrirPaginaPesquisaHTMX() {
        return "vacinas/pesquisar :: formulario";
    }


    @GetMapping("/pesquisar")
    public String pesquisar(VacinaFilter filtro, Model model,
            @PageableDefault(size = 7) @SortDefault(sort = "codigo", direction = Sort.Direction.ASC) Pageable pageable,
            HttpServletRequest request) {
        Page<Vacina> pagina = vacinaRepository.pesquisar(filtro, pageable);
        logger.info("Vacinas pesquisadas: {}", pagina.getContent());
        PageWrapper<Vacina> paginaWrapper = new PageWrapper<>(pagina, request);
        model.addAttribute("pagina", paginaWrapper);
        return "vacinas/vacinas";
    }


    @GetMapping(value = "/pesquisar", headers = "HX-Request")
    public String pesquisarHTMX(VacinaFilter filtro, Model model,
            @PageableDefault(size = 7) @SortDefault(sort = "codigo", direction = Sort.Direction.ASC) Pageable pageable,
            HttpServletRequest request) {
        Page<Vacina> pagina = vacinaRepository.pesquisar(filtro, pageable);
        logger.info("Vacinas pesquisadas: {}", pagina);
        PageWrapper<Vacina> paginaWrapper = new PageWrapper<>(pagina, request);
        model.addAttribute("pagina", paginaWrapper);
        return "vacinas/vacinas :: tabela";
    }

    
    @PostMapping("/abriralterar")
    public String abrirAlterar(Vacina vacina) {
        return "vacinas/alterar";
    }

    @PostMapping(value = "/abriralterar", headers = "HX-Request")
    public String abrirAlterarHTMX(Vacina vacina) {
        return "vacinas/alterar :: formulario";
    }

    @PostMapping("/alterar")
    public String alterar(Vacina vacina) {
        vacinaService.alterar(vacina);
        return "redirect:/vacinas/sucesso2";
    }

    @PostMapping(value = "/alterar", headers = "HX-Request")
    public String alterarHTMX(Vacina vacina, HttpServletResponse response) {
        vacinaService.alterar(vacina);
        response.setHeader("HX-Location", "{\"path\":\"/vacinas/sucesso2\", \"target\":\"#main\", \"swap\":\"outerHTML\"}"); 
        return "mensagem";
    }

    @GetMapping(value = "/sucesso2", headers = "HX-Request")
    public String abrirMensagemSucesso2HTMX(Model model) {
        model.addAttribute("mensagem", "Vacina alterada com sucesso");
        return "mensagem :: texto";
    }

    @GetMapping("/sucesso2")
    public String abrirSucesso2(Model model) {
        model.addAttribute("mensagem", "Alteração de Vacina Efetuada com Sucesso");
        return "mensagem";
    }

    @PostMapping("/confirmarremocao")
    public String confirmarRemocao(Vacina vacina) {
        return "vacinas/confirmarremocao";
    }


    @PostMapping(value = "/confirmarremocao", headers = "HX-Request")
    public String abrirRemoverHTMX(Vacina vacina) {
        return "vacinas/confirmarremocao :: confirmacao";
    }
    
    @PostMapping("/remover")
    public String remover(Vacina vacina) {
        vacina.setStatus(Status.INATIVO);
        vacinaService.alterar(vacina);
        return "redirect:/vacinas/sucesso3";
    }

    @GetMapping("/sucesso3")
    public String abrirSucesso3(Model model) {
        model.addAttribute("mensagem", "Remoção de Vacina Efetuada com Sucesso");
        return "mensagem";
    }

    @PostMapping(value = "/remover", headers = "HX-Request")
    public String removerHTMX(Vacina vacina, HttpServletResponse response) {
        vacina.setStatus(Status.INATIVO);
        vacinaService.alterar(vacina);
        response.setHeader("HX-Location", "{\"path\":\"/vacinas/sucesso3\", \"target\":\"#main\", \"swap\":\"outerHTML\"}"); 
        return "mensagem";
    }

    @GetMapping(value = "/sucesso3", headers = "HX-Request")
    public String abrirMensagemSucesso3HTMX(Model model) {
        model.addAttribute("mensagem", "Vacina removida com sucesso");
        return "mensagem :: texto";
    }

}
